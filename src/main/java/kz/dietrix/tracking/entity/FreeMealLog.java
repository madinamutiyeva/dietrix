package kz.dietrix.tracking.entity;

import jakarta.persistence.*;
import kz.dietrix.auth.entity.User;
import kz.dietrix.common.entity.BaseEntity;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "free_meal_logs", indexes = {
        @Index(name = "idx_free_meal_logs_user_date", columnList = "user_id, logged_on DESC")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FreeMealLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "meal_type", length = 30)
    private String mealType;

    private Integer calories;
    private Integer protein;
    private Integer carbs;
    private Integer fat;

    @Column(name = "logged_on", nullable = false)
    private LocalDate loggedOn;

    @Column(length = 500)
    private String note;
}

