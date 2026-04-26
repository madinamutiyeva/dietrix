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

/**
 * Alias controller exposing tracking endpoints under /api/users/me/* — paths
 * expected by the frontend (matches IMPROVEMENTS.md contract).
 *
 * Original endpoints under /api/tracking/* remain available.
 */
@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
@Tag(name = "User Tracking", description = "Weight, water and free-meal tracking (frontend-friendly aliases)")
public class UserTrackingAliasController {

    private final TrackingService trackingService;

    // ─── Weight ───────────────────────────────────────────────────────────

    @PostMapping("/weight-logs")
    @Operation(summary = "Add or upsert weight log (defaults to today)")
    public ApiResponse<WeightLogDto> addWeight(@Valid @RequestBody WeightLogDto request) {
        return ApiResponse.success("Weight logged", trackingService.addWeight(request));
    }

    @GetMapping("/weight-logs")
    @Operation(summary = "Get all weight logs (newest first)")
    public ApiResponse<List<WeightLogDto>> getWeights() {
        return ApiResponse.success(trackingService.getWeightLogs());
    }

    @GetMapping("/weight-logs/stats")
    @Operation(summary = "Weight chart points + stats. period=7d|30d|90d|365d (default 30d)")
    public ApiResponse<WeightStatsDto> getWeightStats(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) Integer days) {
        int n = days != null ? days : parsePeriod(period);
        return ApiResponse.success(trackingService.getWeightStats(n));
    }

    @DeleteMapping("/weight-logs/{id}")
    @Operation(summary = "Delete a weight log")
    public ApiResponse<Void> deleteWeight(@PathVariable Long id) {
        trackingService.deleteWeight(id);
        return ApiResponse.success("Weight log deleted");
    }

    // ─── Water ────────────────────────────────────────────────────────────

    @PostMapping("/water-logs")
    @Operation(summary = "Add water intake (e.g. +250 ml). Returns updated today status")
    public ApiResponse<WaterStatusDto> addWater(@Valid @RequestBody WaterLogRequest request) {
        return ApiResponse.success("Water logged", trackingService.addWater(request));
    }

    @GetMapping("/water-logs/today")
    @Operation(summary = "Get today's water consumption status")
    public ApiResponse<WaterStatusDto> getWaterToday() {
        return ApiResponse.success(trackingService.getWaterToday());
    }

    @DeleteMapping("/water-logs/today")
    @Operation(summary = "Clear all water log entries for today")
    public ApiResponse<WaterStatusDto> clearWaterToday() {
        return ApiResponse.success("Today's water cleared",
                trackingService.clearWaterForDate(null));
    }

    @DeleteMapping("/water-logs/{id}")
    @Operation(summary = "Delete a water log entry")
    public ApiResponse<Void> deleteWater(@PathVariable Long id) {
        trackingService.deleteWaterLog(id);
        return ApiResponse.success("Water log deleted");
    }

    // ─── Free meal log ────────────────────────────────────────────────────

    @PostMapping("/free-meals")
    @Operation(summary = "Log a meal eaten outside the plan")
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

    // ─── Helpers ─────────────────────────────────────────────────────────

    private int parsePeriod(String period) {
        if (period == null || period.isBlank()) return 30;
        String p = period.trim().toLowerCase();
        try {
            if (p.endsWith("d")) return Integer.parseInt(p.substring(0, p.length() - 1));
            if (p.endsWith("w")) return Integer.parseInt(p.substring(0, p.length() - 1)) * 7;
            if (p.endsWith("m")) return Integer.parseInt(p.substring(0, p.length() - 1)) * 30;
            if (p.endsWith("y")) return Integer.parseInt(p.substring(0, p.length() - 1)) * 365;
            return Integer.parseInt(p);
        } catch (NumberFormatException e) {
            return 30;
        }
    }
}
