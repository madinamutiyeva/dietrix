package kz.dietrix.mealplan.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kz.dietrix.common.dto.ApiResponse;
import kz.dietrix.mealplan.dto.*;
import kz.dietrix.mealplan.service.MealPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/meal-plans")
@RequiredArgsConstructor
@Tag(name = "Meal Plans", description = "Daily meal plan management")
public class MealPlanController {

    private final MealPlanService mealPlanService;

    @GetMapping("/current")
    @Operation(summary = "Get current active daily meal plan")
    public ApiResponse<MealPlanDto> getCurrentPlan() {
        return ApiResponse.success(mealPlanService.getCurrentPlan());
    }

    @PostMapping("/generate")
    @Operation(summary = "Generate a new daily meal plan using AI")
    public ApiResponse<MealPlanDto> generateMealPlan(@RequestBody(required = false) GenerateMealPlanRequest request) {
        if (request == null) request = new GenerateMealPlanRequest();
        return ApiResponse.success("Daily meal plan generated", mealPlanService.generateMealPlan(request));
    }

    @GetMapping("/{planId}")
    @Operation(summary = "Get meal plan by ID")
    public ApiResponse<MealPlanDto> getPlanById(@PathVariable Long planId) {
        return ApiResponse.success(mealPlanService.getPlanById(planId));
    }

    @PostMapping("/{planId}/meals/{mealId}/complete")
    @Operation(summary = "Mark a meal as completed")
    public ApiResponse<MealPlanMealDto> completeMeal(
            @PathVariable Long planId,
            @PathVariable Long mealId) {
        return ApiResponse.success("Meal marked as completed", mealPlanService.completeMeal(planId, mealId));
    }

    @PostMapping("/{planId}/meals")
    @Operation(summary = "Add a meal to an existing plan")
    public ApiResponse<MealPlanMealDto> addMeal(
            @PathVariable Long planId,
            @RequestBody AddMealRequest request) {
        return ApiResponse.success("Meal added", mealPlanService.addMeal(planId, request));
    }

    @PostMapping("/{planId}/meals/generate")
    @Operation(summary = "Generate a single meal using AI and add to plan")
    public ApiResponse<MealPlanMealDto> generateMeal(
            @PathVariable Long planId,
            @RequestBody(required = false) GenerateMealRequest request) {
        if (request == null) request = new GenerateMealRequest();
        return ApiResponse.success("Meal generated", mealPlanService.generateMeal(planId, request));
    }

    @DeleteMapping("/{planId}/meals/{mealId}/complete")
    @Operation(summary = "Mark a meal as uncompleted")
    public ApiResponse<MealPlanMealDto> uncompleteMeal(
            @PathVariable Long planId,
            @PathVariable Long mealId) {
        return ApiResponse.success("Meal marked as uncompleted", mealPlanService.uncompleteMeal(planId, mealId));
    }

    @PatchMapping("/{planId}/meals/{mealId}")
    @Operation(summary = "Update a meal (swap recipe and/or toggle completion)")
    public ApiResponse<MealPlanMealDto> updateMeal(
            @PathVariable Long planId,
            @PathVariable Long mealId,
            @RequestBody UpdateMealRequest request) {
        return ApiResponse.success(mealPlanService.updateMeal(planId, mealId, request));
    }

    @DeleteMapping("/{planId}/meals/{mealId}")
    @Operation(summary = "Remove a meal from the plan")
    public ApiResponse<Void> deleteMeal(
            @PathVariable Long planId,
            @PathVariable Long mealId) {
        mealPlanService.deleteMeal(planId, mealId);
        return ApiResponse.success("Meal removed");
    }

    @GetMapping("/{planId}/shopping-list")
    @Operation(summary = "Get shopping list for a meal plan")
    public ApiResponse<List<ShoppingListItemDto>> getShoppingList(@PathVariable Long planId) {
        return ApiResponse.success(mealPlanService.getShoppingList(planId));
    }

    // ─── History & Calendar ───────────────────────────────────────────────

    @GetMapping("/history")
    @Operation(summary = "Get meal plans history within a date range (default: last 30 days)")
    public ApiResponse<List<MealPlanDto>> getHistory(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ApiResponse.success(mealPlanService.getHistory(from, to));
    }

    @GetMapping("/calendar")
    @Operation(summary = "Get calendar view (one entry per day with status emoji)")
    public ApiResponse<MealPlanCalendarDto> getCalendar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ApiResponse.success(mealPlanService.getCalendar(from, to));
    }

    @GetMapping("/by-date/{date}")
    @Operation(summary = "Get meal plan for a specific date")
    public ApiResponse<MealPlanDto> getPlanByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ApiResponse.success(mealPlanService.getPlanByDate(date));
    }

    @PostMapping("/duplicate-yesterday")
    @Operation(summary = "Create today's plan as a copy of yesterday's plan")
    public ApiResponse<MealPlanDto> duplicateYesterday() {
        return ApiResponse.success("Yesterday's plan duplicated", mealPlanService.duplicateYesterday());
    }
}
