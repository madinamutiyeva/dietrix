package kz.dietrix.userprofile.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kz.dietrix.common.dto.ApiResponse;
import kz.dietrix.recipes.dto.RecipeDto;
import kz.dietrix.recipes.service.RecipeService;
import kz.dietrix.userprofile.dto.*;
import kz.dietrix.userprofile.service.UserProfileService;
import kz.dietrix.userprofile.service.UserSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
@Tag(name = "User Profile", description = "User profile management")
public class UserController {

    private final UserProfileService profileService;
    private final RecipeService recipeService;
    private final UserSettingsService settingsService;

    @GetMapping
    @Operation(summary = "Get current user profile")
    public ApiResponse<UserProfileDto> getProfile() {
        return ApiResponse.success(profileService.getProfile());
    }

    @PatchMapping
    @Operation(summary = "Update current user profile")
    public ApiResponse<UserProfileDto> updateProfile(@RequestBody UpdateProfileRequest request) {
        return ApiResponse.success(profileService.updateProfile(request));
    }

    @GetMapping("/preferences")
    @Operation(summary = "Get user food preferences")
    public ApiResponse<UserPreferenceDto> getPreferences() {
        return ApiResponse.success(profileService.getPreferences());
    }

    @PutMapping("/preferences")
    @Operation(summary = "Update user food preferences")
    public ApiResponse<UserPreferenceDto> updatePreferences(@RequestBody UserPreferenceDto request) {
        return ApiResponse.success(profileService.updatePreferences(request));
    }

    @GetMapping("/targets")
    @Operation(summary = "Get calculated nutritional targets")
    public ApiResponse<UserTargetsDto> getTargets() {
        return ApiResponse.success(profileService.getTargets());
    }

    @GetMapping("/favorites")
    @Operation(summary = "Get favorite recipes")
    public ApiResponse<List<RecipeDto>> getFavorites() {
        return ApiResponse.success(recipeService.getFavorites());
    }

    @PostMapping("/favorites/{recipeId}")
    @Operation(summary = "Add recipe to favorites")
    public ApiResponse<Void> addFavorite(@PathVariable Long recipeId) {
        recipeService.addFavorite(recipeId);
        return ApiResponse.success("Recipe added to favorites");
    }

    @DeleteMapping("/favorites/{recipeId}")
    @Operation(summary = "Remove recipe from favorites")
    public ApiResponse<Void> removeFavorite(@PathVariable Long recipeId) {
        recipeService.removeFavorite(recipeId);
        return ApiResponse.success("Recipe removed from favorites");
    }

    @GetMapping("/settings")
    @Operation(summary = "Get user UI settings (theme/locale/units)")
    public ApiResponse<UserSettingsDto> getSettings() {
        return ApiResponse.success(settingsService.getMine());
    }

    @PatchMapping("/settings")
    @Operation(summary = "Update user UI settings (partial)")
    public ApiResponse<UserSettingsDto> updateSettings(@RequestBody UserSettingsDto request) {
        return ApiResponse.success("Settings updated", settingsService.updateMine(request));
    }
}

