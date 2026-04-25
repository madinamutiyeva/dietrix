package kz.dietrix.notification.push;

import jakarta.persistence.*;
import kz.dietrix.auth.entity.User;
import kz.dietrix.common.entity.BaseEntity;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "device_tokens", indexes = {
        @Index(name = "idx_device_tokens_user", columnList = "user_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceToken extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true, length = 500)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Platform platform;

    @Column(name = "last_used_at", nullable = false)
    @Builder.Default
    private LocalDateTime lastUsedAt = LocalDateTime.now();

    public enum Platform { WEB, IOS, ANDROID }
}

