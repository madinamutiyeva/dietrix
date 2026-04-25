package kz.dietrix.pantry.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kz.dietrix.common.dto.ApiResponse;
import kz.dietrix.pantry.dto.*;
import kz.dietrix.pantry.service.PantryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pantry")
@RequiredArgsConstructor
@Tag(name = "Pantry", description = "Pantry management")
public class PantryController {

    private final PantryService pantryService;

    @GetMapping("/items")
    @Operation(summary = "Get all pantry items")
    public ApiResponse<List<PantryItemDto>> getItems() {
        return ApiResponse.success(pantryService.getItems());
    }

    @GetMapping("/items/search")
    @Operation(summary = "Search pantry items by name/category/expiration window")
    public ApiResponse<List<PantryItemDto>> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer expiringInDays) {
        return ApiResponse.success(pantryService.search(q, category, expiringInDays));
    }

    @PostMapping("/items")
    @Operation(summary = "Add a pantry item")
    public ApiResponse<PantryItemDto> addItem(@Valid @RequestBody PantryItemDto request) {
        return ApiResponse.success("Item added", pantryService.addItem(request));
    }

    @PostMapping("/items/bulk")
    @Operation(summary = "Add multiple pantry items")
    public ApiResponse<List<PantryItemDto>> addItemsBulk(@Valid @RequestBody BulkCreateRequest request) {
        return ApiResponse.success("Items added", pantryService.addItemsBulk(request.getItems()));
    }

    @PatchMapping("/items/{itemId}")
    @Operation(summary = "Update a pantry item")
    public ApiResponse<PantryItemDto> updateItem(@PathVariable Long itemId, @RequestBody PantryItemDto request) {
        return ApiResponse.success(pantryService.updateItem(itemId, request));
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Delete a pantry item")
    public ApiResponse<Void> deleteItem(@PathVariable Long itemId) {
        pantryService.deleteItem(itemId);
        return ApiResponse.success("Item deleted");
    }

    @GetMapping("/summary")
    @Operation(summary = "Get pantry summary")
    public ApiResponse<PantrySummaryDto> getSummary() {
        return ApiResponse.success(pantryService.getSummary());
    }
}

