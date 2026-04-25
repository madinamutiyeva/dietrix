package kz.dietrix.assistant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FaqItemDto {

    private Long id;
    private String question;
    private String answer;
    private String category;
}

