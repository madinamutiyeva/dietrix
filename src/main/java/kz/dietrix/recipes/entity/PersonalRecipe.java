package kz.dietrix.recipes.entity;

import jakarta.persistence.*;
import kz.dietrix.auth.entity.User;
import kz.dietrix.common.entity.BaseEntity;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "personal_recipes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalRecipe extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
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

    @Column(name = "meal_type")
    private String mealType;

    @Column(name = "diet_type")
    private String dietType;

    @OneToMany(mappedBy = "personalRecipe", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PersonalRecipeIngredient> ingredients = new ArrayList<>();

    public void addIngredient(PersonalRecipeIngredient ingredient) {
        ingredients.add(ingredient);
        ingredient.setPersonalRecipe(this);
    }
}

