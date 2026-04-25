package kz.dietrix.recipes.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kz.dietrix.common.dto.ApiResponse;
import kz.dietrix.common.dto.PagedResponse;
import kz.dietrix.recipes.dto.CreatePersonalRecipeRequest;
import kz.dietrix.recipes.dto.RecipeDetailDto;
import kz.dietrix.recipes.dto.RecipeDto;
import kz.dietrix.recipes.service.PersonalRecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recipes/personal")
@RequiredArgsConstructor
@Tag(name = "Personal Recipes", description = "User's own recipes (private)")
public class PersonalRecipeController {

    private final PersonalRecipeService personalRecipeService;

    @GetMapping
    @Operation(summary = "Get my personal recipes")
    public ApiResponse<PagedResponse<RecipeDto>> getMyRecipes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<RecipeDto> recipes = personalRecipeService.getMyRecipes(page, size);
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

    @GetMapping("/{id}")
    @Operation(summary = "Get personal recipe details")
    public ApiResponse<RecipeDetailDto> getMyRecipeDetail(@PathVariable Long id) {
        return ApiResponse.success(personalRecipeService.getMyRecipeDetail(id));
    }

    @PostMapping
    @Operation(summary = "Create a personal recipe")
    public ApiResponse<RecipeDetailDto> createRecipe(@Valid @RequestBody CreatePersonalRecipeRequest request) {
        return ApiResponse.success("Recipe created", personalRecipeService.createRecipe(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a personal recipe")
    public ApiResponse<RecipeDetailDto> updateRecipe(@PathVariable Long id,
                                                      @RequestBody CreatePersonalRecipeRequest request) {
        return ApiResponse.success("Recipe updated", personalRecipeService.updateRecipe(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a personal recipe")
    public ApiResponse<Void> deleteRecipe(@PathVariable Long id) {
        personalRecipeService.deleteRecipe(id);
        return ApiResponse.success("Recipe deleted");
    }
}

