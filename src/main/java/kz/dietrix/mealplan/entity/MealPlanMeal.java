package kz.dietrix.mealplan.entity;

import jakarta.persistence.*;
import kz.dietrix.common.entity.BaseEntity;
import kz.dietrix.recipes.entity.Recipe;
import lombok.*;

@Entity
@Table(name = "meal_plan_meals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealPlanMeal extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_plan_day_id", nullable = false)
    private MealPlanDay mealPlanDay;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type", nullable = false)
    private MealType mealType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    @Builder.Default
    @Column(name = "is_completed")
    private boolean completed = false;

    public enum MealType {
        BREAKFAST, MAIN, SNACK, DESSERT
    }
}

