package kz.dietrix.notification.push;

import kz.dietrix.auth.entity.User;
import kz.dietrix.userprofile.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceTokenService {

    private final DeviceTokenRepository repository;
    private final UserProfileService userProfileService;

    @Transactional
    public void register(RegisterDeviceRequest request) {
        User user = userProfileService.getCurrentUser();
        DeviceToken existing = repository.findByToken(request.getToken()).orElse(null);
        if (existing != null) {
            // re-bind to current user (e.g. user changed accounts on the same device)
            existing.setUser(user);
            existing.setPlatform(request.getPlatform());
            existing.setLastUsedAt(LocalDateTime.now());
            repository.save(existing);
            return;
        }
        repository.save(DeviceToken.builder()
                .user(user)
                .token(request.getToken())
                .platform(request.getPlatform())
                .lastUsedAt(LocalDateTime.now())
                .build());
        log.info("Device registered for user {} ({})", user.getEmail(), request.getPlatform());
    }

    @Transactional
    public void unregister(String token) {
        repository.deleteByToken(token);
    }
}

