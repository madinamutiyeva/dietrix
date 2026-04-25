package kz.dietrix.pantry.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PantryItemDto {

    private Long id;

    @NotBlank(message = "Item name is required")
    private String name;

    private Double quantity;
    private String unit;
    private String category;
    private LocalDate expirationDate;
}

