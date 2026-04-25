package kz.dietrix.assistant.service;

import kz.dietrix.assistant.dto.FaqItemDto;
import kz.dietrix.assistant.entity.FaqItem;
import kz.dietrix.assistant.repository.FaqItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FaqService {

    private final FaqItemRepository faqItemRepository;

    @Transactional(readOnly = true)
    public List<FaqItemDto> getAllFaqs() {
        return faqItemRepository.findAllByOrderByIdAsc().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FaqItemDto> getFaqsByCategory(String category) {
        return faqItemRepository.findByCategoryOrderByIdAsc(category).stream()
                .map(this::toDto)
                .toList();
    }

    private FaqItemDto toDto(FaqItem item) {
        return FaqItemDto.builder()
                .id(item.getId())
                .question(item.getQuestion())
                .answer(item.getAnswer())
                .category(item.getCategory())
                .build();
    }
}

