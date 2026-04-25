package kz.dietrix.userprofile.entity;

import jakarta.persistence.*;
import kz.dietrix.auth.entity.User;
import kz.dietrix.common.entity.BaseEntity;
import lombok.*;

@Entity
@Table(name = "user_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSettings extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Theme theme = Theme.SYSTEM;

    @Column(nullable = false, length = 10)
    @Builder.Default
    private String locale = "ru";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Units units = Units.METRIC;

    public enum Theme  { LIGHT, DARK, SYSTEM }
    public enum Units  { METRIC, IMPERIAL }
}

