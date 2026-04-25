package kz.dietrix.tracking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeightStatsDto {

    private List<Point> points;
    private BigDecimal startWeight;
    private BigDecimal currentWeight;
    private BigDecimal change;          // currentWeight - startWeight
    private BigDecimal min;
    private BigDecimal max;
    private BigDecimal average;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Point {
        private LocalDate date;
        private BigDecimal weightKg;
    }
}

