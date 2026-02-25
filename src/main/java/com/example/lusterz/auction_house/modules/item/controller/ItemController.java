package com.example.lusterz.auction_house.modules.item.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.lusterz.auction_house.common.validation.onCreate;
import com.example.lusterz.auction_house.modules.item.dto.ItemDto;
import com.example.lusterz.auction_house.modules.item.dto.ItemRequest;
import com.example.lusterz.auction_house.modules.item.dto.ItemUpdateRequest;
import com.example.lusterz.auction_house.modules.item.service.ItemService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/{id}")
    public ItemDto getItem(@PathVariable Long id) {
        return itemService.getItem(id);
    }

    @GetMapping 
    public List<ItemDto> getAllItems(@RequestParam(required = false) Long sellerId) {
        if (sellerId != null) {
            return itemService.getAllItemsBySellerId(sellerId);
        }
        return itemService.getAllItems();
    }

    @GetMapping("/active")
    public List<ItemDto> getActiveAuctions() {
        return itemService.getActiveAuctions();
    }

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@Validated(onCreate.class) @RequestBody ItemRequest itemRequest) {
        ItemDto newItem = itemService.createItem(itemRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(newItem);
    }

    @PutMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable Long itemId,@RequestParam Long userId, @Valid @RequestBody ItemUpdateRequest auctionItemRequest) {
        return itemService.updateItem(itemId, userId, auctionItemRequest);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long itemId, @RequestParam Long userId) {
        itemService.deleteItem(itemId, userId);
        return ResponseEntity.noContent().build();
    }
}
