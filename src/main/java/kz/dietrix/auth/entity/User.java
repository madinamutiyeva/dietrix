package kz.dietrix.auth.entity;

import jakarta.persistence.*;
import kz.dietrix.common.entity.BaseEntity;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.USER;

    @Builder.Default
    private boolean enabled = true;

    @Column(name = "reset_token")
    private String resetToken;

    @Column(name = "reset_code_expiry")
    private LocalDateTime resetCodeExpiry;

    public enum Role {
        USER, ADMIN
    }
}

