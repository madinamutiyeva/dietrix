package kz.dietrix.notification.push;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.InputStream;

/**
 * Initializes the FirebaseApp at startup if a service-account file is configured.
 * If not configured, push notifications are silently disabled (FcmPushService becomes a no-op).
 *
 * Configure via:
 *   firebase.enabled=true
 *   firebase.service-account=file:/abs/path/serviceAccount.json   (or classpath:firebase.json)
 */
@Slf4j
@Configuration
public class FirebaseConfig {

    @Value("${firebase.enabled:false}")
    private boolean enabled;

    @Value("${firebase.service-account:}")
    private String serviceAccount;

    private final ResourceLoader resourceLoader;

    public FirebaseConfig(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void init() {
        if (!enabled) {
            log.info("Firebase push notifications are DISABLED (firebase.enabled=false)");
            return;
        }
        if (serviceAccount == null || serviceAccount.isBlank()) {
            log.warn("firebase.enabled=true but firebase.service-account is empty — FCM not initialized");
            return;
        }
        try {
            Resource res = resourceLoader.getResource(serviceAccount);
            try (InputStream in = res.getInputStream()) {
                FirebaseOptions opts = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(in))
                        .build();
                if (FirebaseApp.getApps().isEmpty()) {
                    FirebaseApp.initializeApp(opts);
                    log.info("FirebaseApp initialized successfully");
                }
            }
        } catch (Exception e) {
            log.error("Failed to initialize Firebase: {} — push notifications disabled", e.getMessage());
        }
    }
}

