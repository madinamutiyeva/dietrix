package kz.dietrix.recipes.repository;

import kz.dietrix.recipes.entity.FavoriteRecipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRecipeRepository extends JpaRepository<FavoriteRecipe, Long> {

    List<FavoriteRecipe> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<FavoriteRecipe> findByUserIdAndRecipeId(Long userId, Long recipeId);

    boolean existsByUserIdAndRecipeId(Long userId, Long recipeId);

    void deleteByUserIdAndRecipeId(Long userId, Long recipeId);
}

