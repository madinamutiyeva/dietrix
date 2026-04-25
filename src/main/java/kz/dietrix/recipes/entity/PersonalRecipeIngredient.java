package kz.dietrix.recipes.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "personal_recipe_ingredients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalRecipeIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_recipe_id", nullable = false)
    private PersonalRecipe personalRecipe;

    @Column(nullable = false)
    private String name;

    private String amount;

    private String unit;
}

