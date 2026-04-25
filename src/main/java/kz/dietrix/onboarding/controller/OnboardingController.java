package kz.dietrix.onboarding.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kz.dietrix.common.dto.ApiResponse;
import kz.dietrix.onboarding.dto.*;
import kz.dietrix.onboarding.service.OnboardingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/onboarding")
@RequiredArgsConstructor
@Tag(name = "Onboarding", description = "User onboarding flow")
public class OnboardingController {

    private final OnboardingService onboardingService;

    @GetMapping
    @Operation(summary = "Get onboarding status")
    public ApiResponse<OnboardingStatusDto> getStatus() {
        return ApiResponse.success(onboardingService.getStatus());
    }

    @PutMapping("/basic-info")
    @Operation(summary = "Step 1: Save basic info (gender, age, height, weight)")
    public ApiResponse<OnboardingStatusDto> saveBasicInfo(@Valid @RequestBody BasicInfoRequest request) {
        return ApiResponse.success(onboardingService.saveBasicInfo(request));
    }

    @PutMapping("/activity-goal")
    @Operation(summary = "Step 2: Save activity level and goal")
    public ApiResponse<OnboardingStatusDto> saveActivityGoal(@Valid @RequestBody ActivityGoalRequest request) {
        return ApiResponse.success(onboardingService.saveActivityGoal(request));
    }

    @PutMapping("/preferences")
    @Operation(summary = "Step 3: Save food preferences")
    public ApiResponse<OnboardingStatusDto> savePreferences(@RequestBody PreferencesRequest request) {
        return ApiResponse.success(onboardingService.savePreferences(request));
    }
}

