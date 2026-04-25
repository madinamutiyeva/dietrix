package kz.dietrix.assistant.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kz.dietrix.assistant.dto.ChatRequest;
import kz.dietrix.assistant.dto.ChatResponse;
import kz.dietrix.assistant.dto.FaqItemDto;
import kz.dietrix.assistant.service.AssistantService;
import kz.dietrix.assistant.service.FaqService;
import kz.dietrix.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assistant")
@RequiredArgsConstructor
@Tag(name = "AI Assistant", description = "AI nutrition assistant and FAQ")
public class AssistantController {

    private final AssistantService assistantService;
    private final FaqService faqService;

    @PostMapping("/chat")
    @Operation(summary = "Send a message to the AI assistant")
    public ApiResponse<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        return ApiResponse.success(assistantService.chat(request.getMessage()));
    }

    @GetMapping("/faq")
    @Operation(summary = "Get frequently asked questions")
    public ApiResponse<List<FaqItemDto>> getFaqs(@RequestParam(required = false) String category) {
        if (category != null) {
            return ApiResponse.success(faqService.getFaqsByCategory(category));
        }
        return ApiResponse.success(faqService.getAllFaqs());
    }
}

