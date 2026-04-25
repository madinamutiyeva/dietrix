package kz.dietrix.mealplan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealPlanCalendarDto {

    private LocalDate from;
    private LocalDate to;
    private List<DayEntry> days;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DayEntry {
        private LocalDate date;
        private Long planId;
        private int totalMeals;
        private int completedMeals;
        private String status;     // FULL | PARTIAL | EMPTY
        private String emoji;      // ✅ | ⚠️ | ❌
    }
}

