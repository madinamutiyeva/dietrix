package kz.dietrix.recipes.service;

import kz.dietrix.auth.entity.User;
import kz.dietrix.common.exception.ResourceNotFoundException;
import kz.dietrix.recipes.dto.CreatePersonalRecipeRequest;
import kz.dietrix.recipes.dto.RecipeDetailDto;
import kz.dietrix.recipes.dto.RecipeDto;
import kz.dietrix.recipes.entity.PersonalRecipe;
import kz.dietrix.recipes.entity.PersonalRecipeIngredient;
import kz.dietrix.recipes.repository.PersonalRecipeRepository;
import kz.dietrix.userprofile.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonalRecipeService {

    private final PersonalRecipeRepository personalRecipeRepository;
    private final UserProfileService userProfileService;

    @Transactional(readOnly = true)
    public Page<RecipeDto> getMyRecipes(int page, int size) {
        User user = userProfileService.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size);
        return personalRecipeRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable)
                .map(this::toDto);
    }

    @Transactional(readOnly = true)
    public RecipeDetailDto getMyRecipeDetail(Long id) {
        User user = userProfileService.getCurrentUser();
        PersonalRecipe recipe = personalRecipeRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("PersonalRecipe", "id", id));
        return toDetailDto(recipe);
    }

    @Transactional
    public RecipeDetailDto createRecipe(CreatePersonalRecipeRequest request) {
        User user = userProfileService.getCurrentUser();

        PersonalRecipe recipe = PersonalRecipe.builder()
                .user(user)
                .title(request.getTitle())
                .description(request.getDescription())
                .instructions(request.getInstructions())
                .calories(request.getCalories())
                .protein(request.getProtein())
                .carbs(request.getCarbs())
                .fat(request.getFat())
                .cookTimeMinutes(request.getCookTimeMinutes())
                .cuisine(request.getCuisine())
                .imageUrl(request.getImageUrl())
                .mealType(request.getMealType())
                .dietType(request.getDietType())
                .build();

        if (request.getIngredients() != null) {
            request.getIngredients().forEach(ing -> {
                PersonalRecipeIngredient ingredient = PersonalRecipeIngredient.builder()
                        .name(ing.getName())
                        .amount(ing.getAmount())
                        .unit(ing.getUnit())
                        .build();
                recipe.addIngredient(ingredient);
            });
        }

        personalRecipeRepository.save(recipe);
        log.info("Personal recipe '{}' created by user: {}", recipe.getTitle(), user.getEmail());
        return toDetailDto(recipe);
    }

    @Transactional
    public RecipeDetailDto updateRecipe(Long id, CreatePersonalRecipeRequest request) {
        User user = userProfileService.getCurrentUser();
        PersonalRecipe recipe = personalRecipeRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("PersonalRecipe", "id", id));

        if (request.getTitle() != null) recipe.setTitle(request.getTitle());
        if (request.getDescription() != null) recipe.setDescription(request.getDescription());
        if (request.getInstructions() != null) recipe.setInstructions(request.getInstructions());
        if (request.getCalories() != null) recipe.setCalories(request.getCalories());
        if (request.getProtein() != null) recipe.setProtein(request.getProtein());
        if (request.getCarbs() != null) recipe.setCarbs(request.getCarbs());
        if (request.getFat() != null) recipe.setFat(request.getFat());
        if (request.getCookTimeMinutes() != null) recipe.setCookTimeMinutes(request.getCookTimeMinutes());
        if (request.getCuisine() != null) recipe.setCuisine(request.getCuisine());
        if (request.getImageUrl() != null) recipe.setImageUrl(request.getImageUrl());
        if (request.getMealType() != null) recipe.setMealType(request.getMealType());
        if (request.getDietType() != null) recipe.setDietType(request.getDietType());

        if (request.getIngredients() != null) {
            recipe.getIngredients().clear();
            request.getIngredients().forEach(ing -> {
                PersonalRecipeIngredient ingredient = PersonalRecipeIngredient.builder()
                        .name(ing.getName())
                        .amount(ing.getAmount())
                        .unit(ing.getUnit())
                        .build();
                recipe.addIngredient(ingredient);
            });
        }

        personalRecipeRepository.save(recipe);
        log.info("Personal recipe '{}' updated by user: {}", recipe.getTitle(), user.getEmail());
        return toDetailDto(recipe);
    }

    @Transactional
    public void deleteRecipe(Long id) {
        User user = userProfileService.getCurrentUser();
        PersonalRecipe recipe = personalRecipeRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("PersonalRecipe", "id", id));
        personalRecipeRepository.delete(recipe);
        log.info("Personal recipe '{}' deleted by user: {}", recipe.getTitle(), user.getEmail());
    }

    // ─── Mappers ──────────────────────────────────────────────────────────────

    private RecipeDto toDto(PersonalRecipe r) {
        return RecipeDto.builder()
                .id(r.getId())
                .title(r.getTitle())
                .description(r.getDescription())
                .calories(r.getCalories())
                .protein(r.getProtein())
                .carbs(r.getCarbs())
                .fat(r.getFat())
                .cookTimeMinutes(r.getCookTimeMinutes())
                .cuisine(r.getCuisine())
                .imageUrl(r.getImageUrl())
                .mealType(r.getMealType())
                .favorite(false)
                .build();
    }

    private RecipeDetailDto toDetailDto(PersonalRecipe r) {
        List<RecipeDetailDto.IngredientDto> ingredients = r.getIngredients().stream()
                .map(ing -> RecipeDetailDto.IngredientDto.builder()
                        .id(ing.getId())
                        .name(ing.getName())
                        .amount(ing.getAmount())
                        .unit(ing.getUnit())
                        .build())
                .toList();

        return RecipeDetailDto.builder()
                .id(r.getId())
                .title(r.getTitle())
                .description(r.getDescription())
                .instructions(r.getInstructions())
                .calories(r.getCalories())
                .protein(r.getProtein())
                .carbs(r.getCarbs())
                .fat(r.getFat())
                .cookTimeMinutes(r.getCookTimeMinutes())
                .cuisine(r.getCuisine())
                .imageUrl(r.getImageUrl())
                .mealType(r.getMealType())
                .dietType(r.getDietType())
                .favorite(false)
                .ingredients(ingredients)
                .build();
    }
}

