package kz.dietrix.pantry.service;

import kz.dietrix.auth.entity.User;
import kz.dietrix.common.exception.ResourceNotFoundException;
import kz.dietrix.pantry.dto.*;
import kz.dietrix.pantry.entity.PantryItem;
import kz.dietrix.pantry.repository.PantryItemRepository;
import kz.dietrix.userprofile.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PantryService {

    private final PantryItemRepository pantryItemRepository;
    private final UserProfileService userProfileService;

    @Transactional(readOnly = true)
    public List<PantryItemDto> getItems() {
        User user = userProfileService.getCurrentUser();
        return pantryItemRepository.findByUserIdOrderByNameAsc(user.getId())
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public PantryItemDto addItem(PantryItemDto dto) {
        User user = userProfileService.getCurrentUser();
        PantryItem item = PantryItem.builder()
                .user(user)
                .name(dto.getName())
                .quantity(dto.getQuantity())
                .unit(dto.getUnit())
                .category(dto.getCategory())
                .expirationDate(dto.getExpirationDate())
                .build();
        item = pantryItemRepository.save(item);
        log.info("Pantry item added: {} for user: {}", item.getName(), user.getEmail());
        return toDto(item);
    }

    @Transactional
    public List<PantryItemDto> addItemsBulk(List<PantryItemDto> dtos) {
        User user = userProfileService.getCurrentUser();
        List<PantryItem> items = dtos.stream()
                .map(dto -> PantryItem.builder()
                        .user(user)
                        .name(dto.getName())
                        .quantity(dto.getQuantity())
                        .unit(dto.getUnit())
                        .category(dto.getCategory())
                        .expirationDate(dto.getExpirationDate())
                        .build())
                .toList();
        items = pantryItemRepository.saveAll(items);
        log.info("{} pantry items added in bulk for user: {}", items.size(), user.getEmail());
        return items.stream().map(this::toDto).toList();
    }

    @Transactional
    public PantryItemDto updateItem(Long itemId, PantryItemDto dto) {
        User user = userProfileService.getCurrentUser();
        PantryItem item = pantryItemRepository.findByIdAndUserId(itemId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("PantryItem", "id", itemId));

        if (dto.getName() != null) item.setName(dto.getName());
        if (dto.getQuantity() != null) item.setQuantity(dto.getQuantity());
        if (dto.getUnit() != null) item.setUnit(dto.getUnit());
        if (dto.getCategory() != null) item.setCategory(dto.getCategory());
        if (dto.getExpirationDate() != null) item.setExpirationDate(dto.getExpirationDate());

        item = pantryItemRepository.save(item);
        return toDto(item);
    }

    @Transactional
    public void deleteItem(Long itemId) {
        User user = userProfileService.getCurrentUser();
        pantryItemRepository.findByIdAndUserId(itemId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("PantryItem", "id", itemId));
        pantryItemRepository.deleteByIdAndUserId(itemId, user.getId());
        log.info("Pantry item deleted: {} for user: {}", itemId, user.getEmail());
    }

    @Transactional(readOnly = true)
    public PantrySummaryDto getSummary() {
        User user = userProfileService.getCurrentUser();
        List<PantryItem> items = pantryItemRepository.findByUserIdOrderByNameAsc(user.getId());

        Map<String, Long> byCategory = items.stream()
                .filter(i -> i.getCategory() != null)
                .collect(Collectors.groupingBy(PantryItem::getCategory, Collectors.counting()));

        long expiring = items.stream()
                .filter(i -> i.getExpirationDate() != null)
                .filter(i -> i.getExpirationDate().isBefore(LocalDate.now().plusDays(3)))
                .count();

        return PantrySummaryDto.builder()
                .totalItems(items.size())
                .categories(pantryItemRepository.findDistinctCategoriesByUserId(user.getId()))
                .itemsByCategory(byCategory)
                .expiringItems(expiring)
                .build();
    }

    public List<String> getPantryIngredientNames() {
        User user = userProfileService.getCurrentUser();
        return pantryItemRepository.findByUserIdOrderByNameAsc(user.getId())
                .stream()
                .map(PantryItem::getName)
                .toList();
    }

    private PantryItemDto toDto(PantryItem item) {
        return PantryItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .quantity(item.getQuantity())
                .unit(item.getUnit())
                .category(item.getCategory())
                .expirationDate(item.getExpirationDate())
                .build();
    }

    @Transactional(readOnly = true)
    public List<PantryItemDto> search(String q, String category, Integer expiringInDays) {
        User user = userProfileService.getCurrentUser();
        String query = (q == null || q.isBlank()) ? null : q.trim();
        String cat   = (category == null || category.isBlank()) ? null : category;
        LocalDate cutoff = expiringInDays != null
                ? LocalDate.now().plusDays(expiringInDays)
                : null;
        return pantryItemRepository.search(user.getId(), query, cat, cutoff)
                .stream().map(this::toDto).toList();
    }
}

