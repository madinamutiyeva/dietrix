package kz.dietrix.tracking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kz.dietrix.common.dto.ApiResponse;
import kz.dietrix.tracking.dto.*;
import kz.dietrix.tracking.service.TrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tracking")
@RequiredArgsConstructor
@Tag(name = "Tracking", description = "Weight, water and free-meal tracking")
public class TrackingController {

    private final TrackingService trackingService;

    // ─── Weight ───────────────────────────────────────────────────────────

    @PostMapping("/weight")
    @Operation(summary = "Add or upsert weight log for a date (defaults to today)")
    public ApiResponse<WeightLogDto> addWeight(@Valid @RequestBody WeightLogDto request) {
        return ApiResponse.success("Weight logged", trackingService.addWeight(request));
    }

    @GetMapping("/weight")
    @Operation(summary = "Get all weight logs (newest first)")
    public ApiResponse<List<WeightLogDto>> getWeights() {
        return ApiResponse.success(trackingService.getWeightLogs());
    }

    @GetMapping("/weight/stats")
    @Operation(summary = "Get weight chart points and stats for last N days")
    public ApiResponse<WeightStatsDto> getWeightStats(@RequestParam(defaultValue = "30") int days) {
        return ApiResponse.success(trackingService.getWeightStats(days));
    }

    @DeleteMapping("/weight/{id}")
    @Operation(summary = "Delete a weight log")
    public ApiResponse<Void> deleteWeight(@PathVariable Long id) {
        trackingService.deleteWeight(id);
        return ApiResponse.success("Weight log deleted");
    }

    // ─── Water ───────────────────────────────────────────────────────────

    @PostMapping("/water")
    @Operation(summary = "Add water intake (e.g. +250 ml). Returns updated today status")
    public ApiResponse<WaterStatusDto> addWater(@Valid @RequestBody WaterLogRequest request) {
        return ApiResponse.success("Water logged", trackingService.addWater(request));
    }

    @GetMapping("/water/today")
    @Operation(summary = "Get today's water consumption status")
    public ApiResponse<WaterStatusDto> getWaterToday() {
        return ApiResponse.success(trackingService.getWaterToday());
    }

    @DeleteMapping("/water/today")
    @Operation(summary = "Clear all water log entries for today")
    public ApiResponse<WaterStatusDto> clearWaterToday() {
        return ApiResponse.success("Today's water cleared",
                trackingService.clearWaterForDate(null));
    }

    @DeleteMapping("/water/{id}")
    @Operation(summary = "Delete a water log entry")
    public ApiResponse<Void> deleteWater(@PathVariable Long id) {
        trackingService.deleteWaterLog(id);
        return ApiResponse.success("Water log deleted");
    }

    // ─── Free meal log ────────────────────────────────────────────────────

    @PostMapping("/free-meals")
    @Operation(summary = "Log a meal eaten outside the plan (e.g. cafe)")
    public ApiResponse<FreeMealLogDto> addFreeMeal(@Valid @RequestBody FreeMealLogDto request) {
        return ApiResponse.success("Meal logged", trackingService.addFreeMeal(request));
    }

    @GetMapping("/free-meals")
    @Operation(summary = "Get free meals for a specific date (defaults to today)")
    public ApiResponse<List<FreeMealLogDto>> getFreeMeals(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (date == null) date = LocalDate.now();
        return ApiResponse.success(trackingService.getFreeMealsForDate(date));
    }

    @DeleteMapping("/free-meals/{id}")
    @Operation(summary = "Delete a free-meal log entry")
    public ApiResponse<Void> deleteFreeMeal(@PathVariable Long id) {
        trackingService.deleteFreeMeal(id);
        return ApiResponse.success("Free meal log deleted");
    }
}
