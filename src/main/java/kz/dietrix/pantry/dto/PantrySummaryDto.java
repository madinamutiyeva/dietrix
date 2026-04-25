package kz.dietrix.pantry.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PantrySummaryDto {

    private long totalItems;
    private List<String> categories;
    private Map<String, Long> itemsByCategory;
    private long expiringItems;
}

