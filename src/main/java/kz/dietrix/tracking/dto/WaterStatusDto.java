package kz.dietrix.tracking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaterStatusDto {
    private LocalDate date;
    private int consumedMl;
    private int targetMl;
    private double percent;
}

