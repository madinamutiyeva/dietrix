package kz.dietrix.recipes.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kz.dietrix.common.dto.ApiResponse;
import kz.dietrix.common.dto.PagedResponse;
import kz.dietrix.recipes.dto.*;
import kz.dietrix.recipes.entity.Recipe;
import kz.dietrix.recipes.service.RecipeGenerationService;
import kz.dietrix.recipes.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
@Tag(name = "Recipes", description = "AI-generated shared recipes (public for all users)")
public class RecipeController {

    private final RecipeService recipeService;
    private final RecipeGenerationService recipeGenerationService;

    @GetMapping
    @Operation(summary = "Get all recipes with filters")
    public ApiResponse<PagedResponse<RecipeDto>> getRecipes(
            @RequestParam(required = false) String cuisine,
            @RequestParam(required = false) Integer maxCalories,
            @RequestParam(required = false) String mealType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<RecipeDto> recipes = recipeService.getRecipes(cuisine, maxCalories, mealType, page, size);
        PagedResponse<RecipeDto> response = PagedResponse.<RecipeDto>builder()
                .content(recipes.getContent())
                .page(recipes.getNumber())
                .size(recipes.getSize())
                .totalElements(recipes.getTotalElements())
                .totalPages(recipes.getTotalPages())
                .last(recipes.isLast())
                .build();
        return ApiResponse.success(response);
    }

    @GetMapping("/recommended")
    @Operation(summary = "Get recommended recipes")
    public ApiResponse<List<RecipeDto>> getRecommended() {
        return ApiResponse.success(recipeService.getRecommended());
    }

    @PostMapping("/generate")
    @Operation(summary = "Generate a recipe using AI")
    public ApiResponse<RecipeDetailDto> generateRecipe(@RequestBody GenerateRecipeRequest request) {
        Recipe recipe = recipeGenerationService.generateRecipe(request);
        RecipeDetailDto detail = recipeService.getRecipeDetail(recipe.getId());
        return ApiResponse.success("Recipe generated successfully", detail);
    }

    @GetMapping("/recent-generated")
    @Operation(summary = "Get recently generated recipes")
    public ApiResponse<List<RecipeDto>> getRecentGenerated() {
        return ApiResponse.success(recipeService.getRecentGenerated());
    }

    @GetMapping("/{recipeId}")
    @Operation(summary = "Get recipe details")
    public ApiResponse<RecipeDetailDto> getRecipeDetail(@PathVariable Long recipeId) {
        return ApiResponse.success(recipeService.getRecipeDetail(recipeId));
    }
}

