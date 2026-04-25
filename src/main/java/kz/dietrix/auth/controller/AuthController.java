package kz.dietrix.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kz.dietrix.auth.dto.*;
import kz.dietrix.auth.service.AuthService;
import kz.dietrix.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Auth endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @Operation(summary = "Register a new user")
    public ApiResponse<AuthResponse> signup(@Valid @RequestBody SignupRequest request) {
        return ApiResponse.success("Registration successful", authService.signup(request));
    }

    @PostMapping("/signin")
    @Operation(summary = "Sign in with email and password")
    public ApiResponse<AuthResponse> signin(@Valid @RequestBody SigninRequest request) {
        return ApiResponse.success("Login successful", authService.signin(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token")
    public ApiResponse<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ApiResponse.success(authService.refreshToken(request));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout and revoke refresh token")
    public ApiResponse<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request);
        return ApiResponse.success("Logged out successfully");
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset — sends 6-digit code to email")
    public ApiResponse<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ApiResponse.success("If the email exists, a 6-digit code has been sent");
    }

    @PostMapping("/verify-reset-code")
    @Operation(summary = "Verify 6-digit reset code")
    public ApiResponse<Void> verifyResetCode(@Valid @RequestBody VerifyResetCodeRequest request) {
        authService.verifyResetCode(request);
        return ApiResponse.success("Code is valid");
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password using verified 6-digit code")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ApiResponse.success("Password reset successful");
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change password (authenticated user). Revokes all refresh tokens.")
    public ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return ApiResponse.success("Password changed successfully");
    }

    @DeleteMapping("/account")
    @Operation(summary = "Permanently delete the current account (GDPR). Requires password.")
    public ApiResponse<Void> deleteAccount(@Valid @RequestBody DeleteAccountRequest request) {
        authService.deleteAccount(request);
        return ApiResponse.success("Account deleted");
    }
}

