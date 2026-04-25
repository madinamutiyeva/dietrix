package kz.dietrix.mealplan.entity;

import jakarta.persistence.*;
import kz.dietrix.auth.entity.User;
import kz.dietrix.common.entity.BaseEntity;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "meal_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealPlan extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "week_start_date", nullable = false)
    private LocalDate weekStartDate;

    @Column(name = "week_end_date", nullable = false)
    private LocalDate weekEndDate;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MealPlanStatus status = MealPlanStatus.ACTIVE;

    @OneToMany(mappedBy = "mealPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MealPlanDay> days = new ArrayList<>();

    @OneToMany(mappedBy = "mealPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ShoppingListItem> shoppingList = new ArrayList<>();

    public enum MealPlanStatus {
        ACTIVE, COMPLETED, ARCHIVED
    }
}

