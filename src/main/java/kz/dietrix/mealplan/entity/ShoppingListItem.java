package kz.dietrix.mealplan.entity;

import jakarta.persistence.*;
import kz.dietrix.common.entity.BaseEntity;
import lombok.*;

@Entity
@Table(name = "shopping_list_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShoppingListItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_plan_id", nullable = false)
    private MealPlan mealPlan;

    @Column(nullable = false)
    private String name;

    private String amount;

    private String unit;

    @Builder.Default
    @Column(name = "is_purchased")
    private boolean purchased = false;
}

