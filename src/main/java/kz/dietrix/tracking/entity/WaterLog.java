package kz.dietrix.tracking.entity;

import jakarta.persistence.*;
import kz.dietrix.auth.entity.User;
import kz.dietrix.common.entity.BaseEntity;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "water_logs", indexes = {
        @Index(name = "idx_water_logs_user_date", columnList = "user_id, logged_on DESC")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WaterLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "amount_ml", nullable = false)
    private Integer amountMl;

    @Column(name = "logged_on", nullable = false)
    private LocalDate loggedOn;
}

