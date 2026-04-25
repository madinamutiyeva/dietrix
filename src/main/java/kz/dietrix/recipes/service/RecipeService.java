package kz.dietrix.recipes.service;

import kz.dietrix.auth.entity.User;
import kz.dietrix.common.exception.ResourceNotFoundException;
import kz.dietrix.recipes.dto.RecipeDetailDto;
import kz.dietrix.recipes.dto.RecipeDto;
import kz.dietrix.recipes.entity.FavoriteRecipe;
import kz.dietrix.recipes.entity.Recipe;
import kz.dietrix.recipes.repository.FavoriteRecipeRepository;
import kz.dietrix.recipes.repository.RecipeRepository;
import kz.dietrix.userprofile.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final FavoriteRecipeRepository favoriteRecipeRepository;
    private final UserProfileService userProfileService;

    @Transactional(readOnly = true)
    public Page<RecipeDto> getRecipes(String cuisine, Integer maxCalories, String mealType, int page, int size) {
        User user = userProfileService.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        String normalizedMealType = mealType != null ? mealType.toLowerCase() : null;
        Page<Recipe> recipes = recipeRepository.findFiltered(cuisine, maxCalories, normalizedMealType, pageable);
        return recipes.map(r -> toDto(r, user.getId()));
    }

    @Transactional(readOnly = true)
    public List<RecipeDto> getRecommended() {
        User user = userProfileService.getCurrentUser();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Page<Recipe> recipes = recipeRepository.findAll(pageable);
        return recipes.map(r -> toDto(r, user.getId())).getContent();
    }

    @Transactional(readOnly = true)
    public List<RecipeDto> getRecentGenerated() {
        User user = userProfileService.getCurrentUser();
        Pageable pageable = PageRequest.of(0, 10);
        return recipeRepository.findRecentGeneratedByUserId(user.getId(), pageable)
                .stream()
                .map(r -> toDto(r, user.getId()))
                .toList();
    }

    @Transactional(readOnly = true)
    public RecipeDetailDto getRecipeDetail(Long recipeId) {
        User user = userProfileService.getCurrentUser();
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe", "id", recipeId));

        boolean isFav = favoriteRecipeRepository.existsByUserIdAndRecipeId(user.getId(), recipeId);

        List<RecipeDetailDto.IngredientDto> ingredients = recipe.getIngredients().stream()
                .map(ing -> RecipeDetailDto.IngredientDto.builder()
                        .id(ing.getId())
                        .name(ing.getName())
                        .amount(ing.getAmount())
                        .unit(ing.getUnit())
                        .build())
                .toList();

        return RecipeDetailDto.builder()
                .id(recipe.getId())
                .title(recipe.getTitle())
                .description(recipe.getDescription())
                .instructions(recipe.getInstructions())
                .calories(recipe.getCalories())
                .protein(recipe.getProtein())
                .carbs(recipe.getCarbs())
                .fat(recipe.getFat())
                .cookTimeMinutes(recipe.getCookTimeMinutes())
                .cuisine(recipe.getCuisine())
                .imageUrl(recipe.getImageUrl())
                .mealType(recipe.getMealType())
                .dietType(recipe.getDietType())
                .favorite(isFav)
                .ingredients(ingredients)
                .build();
    }

    @Transactional(readOnly = true)
    public List<RecipeDto> getFavorites() {
        User user = userProfileService.getCurrentUser();
        return favoriteRecipeRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(fav -> toDto(fav.getRecipe(), user.getId(), true))
                .toList();
    }

    @Transactional
    public void addFavorite(Long recipeId) {
        User user = userProfileService.getCurrentUser();
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe", "id", recipeId));

        if (!favoriteRecipeRepository.existsByUserIdAndRecipeId(user.getId(), recipeId)) {
            FavoriteRecipe fav = FavoriteRecipe.builder()
                    .user(user)
                    .recipe(recipe)
                    .build();
            favoriteRecipeRepository.save(fav);
            log.info("Recipe {} added to favorites for user {}", recipeId, user.getEmail());
        }
    }

    @Transactional
    public void removeFavorite(Long recipeId) {
        User user = userProfileService.getCurrentUser();
        favoriteRecipeRepository.deleteByUserIdAndRecipeId(user.getId(), recipeId);
        log.info("Recipe {} removed from favorites for user {}", recipeId, user.getEmail());
    }

    private RecipeDto toDto(Recipe recipe, Long userId) {
        boolean isFav = favoriteRecipeRepository.existsByUserIdAndRecipeId(userId, recipe.getId());
        return toDto(recipe, userId, isFav);
    }

    private RecipeDto toDto(Recipe recipe, Long userId, boolean isFav) {
        return RecipeDto.builder()
                .id(recipe.getId())
                .title(recipe.getTitle())
                .description(recipe.getDescription())
                .calories(recipe.getCalories())
                .protein(recipe.getProtein())
                .carbs(recipe.getCarbs())
                .fat(recipe.getFat())
                .cookTimeMinutes(recipe.getCookTimeMinutes())
                .cuisine(recipe.getCuisine())
                .imageUrl(recipe.getImageUrl())
                .mealType(recipe.getMealType())
                .favorite(isFav)
                .build();
    }
}

