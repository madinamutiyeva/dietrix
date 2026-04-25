package kz.dietrix.tracking.entity;

import jakarta.persistence.*;
import kz.dietrix.auth.entity.User;
import kz.dietrix.common.entity.BaseEntity;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "weight_logs", uniqueConstraints = {
        @UniqueConstraint(name = "uk_weight_user_day", columnNames = {"user_id", "logged_on"})
}, indexes = {
        @Index(name = "idx_weight_logs_user_date", columnList = "user_id, logged_on DESC")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeightLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "weight_kg", nullable = false, precision = 5, scale = 2)
    private BigDecimal weightKg;

    @Column(name = "logged_on", nullable = false)
    private LocalDate loggedOn;

    @Column(length = 500)
    private String note;
}

