package kz.dietrix.tracking.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeightLogDto {
    private Long id;

    @NotNull
    @DecimalMin("20.0")
    @DecimalMax("400.0")
    private BigDecimal weightKg;

    private LocalDate loggedOn;   // если null — сервер ставит сегодня
    private String note;
}

