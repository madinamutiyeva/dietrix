package kz.dietrix.recipes.entity;

import jakarta.persistence.*;
import kz.dietrix.auth.entity.User;
import kz.dietrix.common.entity.BaseEntity;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recipes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recipe extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String instructions;

    private Integer calories;
    private Integer protein;
    private Integer carbs;
    private Integer fat;

    @Column(name = "cook_time_minutes")
    private Integer cookTimeMinutes;

    private String cuisine;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "is_generated")
    @Builder.Default
    private boolean generated = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generated_for_user_id")
    private User generatedForUser;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RecipeIngredient> ingredients = new ArrayList<>();

    @Column(name = "meal_type")
    private String mealType;

    @Column(name = "diet_type")
    private String dietType;

    public void addIngredient(RecipeIngredient ingredient) {
        ingredients.add(ingredient);
        ingredient.setRecipe(this);
    }
}

