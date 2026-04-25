package kz.dietrix.mealplan.entity;

import jakarta.persistence.*;
import kz.dietrix.common.entity.BaseEntity;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "meal_plan_days")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealPlanDay extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_plan_id", nullable = false)
    private MealPlan mealPlan;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @OneToMany(mappedBy = "mealPlanDay", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MealPlanMeal> meals = new ArrayList<>();
}

