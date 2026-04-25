package kz.dietrix.dashboard.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kz.dietrix.common.dto.ApiResponse;
import kz.dietrix.dashboard.dto.DashboardDto;
import kz.dietrix.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Aggregated dashboard data")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    @Operation(summary = "Get dashboard with aggregated user data")
    public ApiResponse<DashboardDto> getDashboard() {
        return ApiResponse.success(dashboardService.getDashboard());
    }
}

