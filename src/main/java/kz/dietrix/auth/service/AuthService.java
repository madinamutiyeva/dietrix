package kz.dietrix.auth.service;

import kz.dietrix.auth.dto.*;
import kz.dietrix.auth.entity.RefreshToken;
import kz.dietrix.auth.entity.User;
import kz.dietrix.auth.repository.UserRepository;
import kz.dietrix.auth.security.JwtTokenProvider;
import kz.dietrix.common.exception.BadRequestException;
import kz.dietrix.common.exception.ConflictException;
import kz.dietrix.common.exception.ResourceNotFoundException;
import kz.dietrix.common.service.EmailService;
import kz.dietrix.userprofile.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final EmailService emailService;
    private final UserProfileService userProfileService;

    @Transactional
    public AuthResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email is already registered");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.USER)
                .enabled(true)
                .build();

        user = userRepository.save(user);
        log.info("New user registered: {}", user.getEmail());

        String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return AuthResponse.of(accessToken, refreshToken.getToken(),
                user.getId(), user.getEmail(), user.getName());
    }

    @Transactional
    public AuthResponse signin(SigninRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));

        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        log.info("User signed in: {}", user.getEmail());
        return AuthResponse.of(accessToken, refreshToken.getToken(),
                user.getId(), user.getEmail(), user.getName());
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenService.findByToken(request.getRefreshToken());
        refreshTokenService.verifyExpiration(refreshToken);

        User user = refreshToken.getUser();
        String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail());

        // Rotate refresh token
        refreshTokenService.deleteByToken(request.getRefreshToken());
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);

        return AuthResponse.of(accessToken, newRefreshToken.getToken(),
                user.getId(), user.getEmail(), user.getName());
    }

    @Transactional
    public void logout(RefreshTokenRequest request) {
        refreshTokenService.deleteByToken(request.getRefreshToken());
        log.info("User logged out, refresh token revoked");
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        userRepository.findByEmail(request.getEmail().toLowerCase()).ifPresent(user -> {
            String code = String.format("%06d", new SecureRandom().nextInt(1_000_000));
            user.setResetToken(code);
            user.setResetCodeExpiry(LocalDateTime.now().plusMinutes(15));
            userRepository.save(user);

            emailService.sendResetCodeEmail(user.getEmail(), user.getName(), code);
            log.info("Reset code sent to: {}", user.getEmail());
        });
        // Always return success to prevent email enumeration
    }

    @Transactional(readOnly = true)
    public void verifyResetCode(VerifyResetCodeRequest request) {
        User user = userRepository.findByEmail(request.getEmail().toLowerCase())
                .orElseThrow(() -> new BadRequestException("Invalid code"));

        validateCode(user, request.getCode());
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail().toLowerCase())
                .orElseThrow(() -> new BadRequestException("Invalid code"));

        validateCode(user, request.getCode());

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null);
        user.setResetCodeExpiry(null);
        userRepository.save(user);

        refreshTokenService.deleteByUser(user);
        log.info("Password reset for user: {}", user.getEmail());
    }

    private void validateCode(User user, String code) {
        if (user.getResetToken() == null || !user.getResetToken().equals(code)) {
            throw new BadRequestException("Invalid or incorrect code");
        }
        if (user.getResetCodeExpiry() == null || LocalDateTime.now().isAfter(user.getResetCodeExpiry())) {
            throw new BadRequestException("Code has expired. Please request a new one");
        }
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        User user = userProfileService.getCurrentUser();
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new BadRequestException("New password must differ from the current one");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        refreshTokenService.deleteByUser(user);
        log.info("Password changed for user: {}", user.getEmail());
    }

    @Transactional
    public void deleteAccount(DeleteAccountRequest request) {
        User user = userProfileService.getCurrentUser();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Password is incorrect");
        }
        refreshTokenService.deleteByUser(user);
        userRepository.delete(user); // cascades to profile, plans, pantry, notifications, etc.
        log.warn("Account deleted: {}", user.getEmail());
    }
}

