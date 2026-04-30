package com.example.lusterz.auction_house.unit.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.example.lusterz.auction_house.TestData;
import com.example.lusterz.auction_house.common.exception.ItemException;
import com.example.lusterz.auction_house.common.exception.UserException;
import com.example.lusterz.auction_house.modules.bid.repository.BidRepository;
import com.example.lusterz.auction_house.modules.item.dto.ItemDto;
import com.example.lusterz.auction_house.modules.item.dto.ItemRequest;
import com.example.lusterz.auction_house.modules.item.dto.ItemUpdateRequest;
import com.example.lusterz.auction_house.modules.item.mapper.ItemMapper;
import com.example.lusterz.auction_house.modules.item.model.AuctionStatus;
import com.example.lusterz.auction_house.modules.item.model.Item;
import com.example.lusterz.auction_house.modules.item.repository.ItemRepository;
import com.example.lusterz.auction_house.modules.item.service.ItemService;
import com.example.lusterz.auction_house.modules.user.model.User;
import com.example.lusterz.auction_house.modules.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    
    @Mock private UserRepository userRepository;
    @Mock private ItemRepository itemRepository;
    @Mock private BidRepository bidRepository;
    @Mock private ApplicationEventPublisher eventPublisher;

    @Spy
    private ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

    @InjectMocks
    private ItemService itemService;

    @Test
    void getItem_ReturnItemDto_WhenFound() {
        Long id = 1L;
        Item item = TestData.testItem(id, AuctionStatus.ACTIVE);
        ItemDto dto = TestData.testItemDto(item);

        when(itemRepository.findById(id)).thenReturn(Optional.of(item));

        ItemDto result = itemService.getItem(id);

        assertEquals(dto, result);

        verify(itemRepository).findById(id);
        verify(itemMapper).toDto(item);
    }

    @Test
    void getItem_ThrowItemExceptionNotFound_WhenNotFound() {
        Long id = 1L;

        when(itemRepository.findById(id)).thenReturn(Optional.empty());

        ItemException.NotFound ex = assertThrows(ItemException.NotFound.class, () -> itemService.getItem(id));
        assertTrue(ex.getMessage().contains(id.toString()));

        verify(itemRepository).findById(id);
        verifyNoInteractions(itemMapper);
    }

    @Test
    void getAllItems_ReturnDtoList_WhenFound() {
        Item item = TestData.testItem(1L, AuctionStatus.ACTIVE);
        ItemDto dto = TestData.testItemDto(item);

        when(itemRepository.findAll()).thenReturn(List.of(item));

        List<ItemDto> result = itemService.getAllItems();

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));

        verify(itemRepository).findAll();
        verify(itemMapper, times(1)).toDto(any(Item.class));
    }

    @Test
    void getAllItems_ReturnEmptyList_WhenNotFound() {

        when(itemRepository.findAll()).thenReturn(List.of());

        List<ItemDto> result = itemService.getAllItems();

        assertTrue(result.isEmpty());

        verify(itemRepository).findAll();
        verifyNoInteractions(itemMapper);
    }

    @Test
    void getActiveAuctions_ReturnDtoList_WhenFound() {
        AuctionStatus status = AuctionStatus.ACTIVE;
        Item item = TestData.testItem(1L, status);
        ItemDto dto = TestData.testItemDto(item);

        when(itemRepository.findAllByStatus(status)).thenReturn(List.of(item));

        List<ItemDto> result = itemService.getActiveAuctions();

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));

        verify(itemRepository).findAllByStatus(status);
        verify(itemMapper, times(1)).toDto(any(Item.class));
    }

    @Test
    void getActiveAuctions_ReturnEmptyList_WhenNotFound() {
        AuctionStatus status = AuctionStatus.ACTIVE;

        when(itemRepository.findAllByStatus(status)).thenReturn(List.of());

        List<ItemDto> result = itemService.getActiveAuctions();

        assertTrue(result.isEmpty());

        verify(itemRepository).findAllByStatus(status);
        verifyNoInteractions(itemMapper);
    }

    @Test
    void getAllItemsBySellerId_ReturnDtoList_WhenFoundAndIdExists() {
        Long sellerId = 1L;
        Item item = TestData.testItem(1L, AuctionStatus.ACTIVE);
        ItemDto dto = TestData.testItemDto(item);

        when(userRepository.existsById(sellerId)).thenReturn(true);
        when(itemRepository.findAllBySellerId(sellerId)).thenReturn(List.of(item));

        List<ItemDto> result = itemService.getAllItemsBySellerId(sellerId);

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));

        verify(userRepository).existsById(sellerId);
        verify(itemRepository).findAllBySellerId(sellerId);
        verify(itemMapper, times(1)).toDto(item);
    }

    @Test
    void getAllItemsBySellerId_ReturnEmptyList_WhenNotFoundAndIdExists() {
        Long sellerId = 1L;

        when(userRepository.existsById(sellerId)).thenReturn(true);
        when(itemRepository.findAllBySellerId(sellerId)).thenReturn(List.of());

        List<ItemDto> result = itemService.getAllItemsBySellerId(sellerId);

        assertTrue(result.isEmpty());

        verify(userRepository).existsById(sellerId);
        verify(itemRepository).findAllBySellerId(sellerId);
        verifyNoInteractions(itemMapper);
    }

    @Test
    void getAllItemsBySellerId_ThrowUserExceptionNotFound_WhenIdNotFound() {
        Long id = 1L;

        when(userRepository.existsById(id)).thenReturn(false);

        UserException.NotFound ex = assertThrows(UserException.NotFound.class, () -> itemService.getAllItemsBySellerId(id));
        assertTrue(ex.getMessage().contains(id.toString()));

        verify(userRepository).existsById(id);
        verifyNoInteractions(itemRepository);
        verifyNoInteractions(itemMapper);
    }

    @Test
    void createItem_SaveItemAndReturnDto_WhenValidRequestAndUserExists() {
        Long id = 1L;
        User user = TestData.testUser(id, false);
        ItemRequest request = TestData.testItemRequest(
            id,
            OffsetDateTime.now(),
            OffsetDateTime.now().plusMinutes(1)
        );

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        ItemDto result = itemService.createItem(request);

        assertEquals(request.title(), result.title());
        assertEquals(request.description(), result.description());
        assertEquals(request.itemImageUrl(), result.itemImageUrl());
        assertEquals(request.startingPrice(), result.startingPrice());
        assertEquals(request.startTime(), result.startTime());
        assertEquals(request.endTime(), result.endTime());
        assertEquals(AuctionStatus.PENDING, result.status());
        assertEquals(request.startingPrice(), result.currentHighestBid());

        verify(userRepository).findById(id);
        verify(itemRepository).save(any(Item.class));
        verify(itemMapper).toDto(any(Item.class));
    }

    @Test
    void createItem_ThrowUserExceptionNotFound_WhenUserNotFound() {
        Long id = 1L;
        ItemRequest request = TestData.testItemRequest(
            id,
            OffsetDateTime.now(),
            OffsetDateTime.now().plusMinutes(1)
        );

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        UserException.NotFound ex = assertThrows(UserException.NotFound.class, () -> itemService.createItem(request));
        assertTrue(ex.getMessage().contains(id.toString()));

        verify(userRepository).findById(id);
        verifyNoInteractions(itemRepository);
        verifyNoInteractions(itemMapper);
    }

    @Test
    void createItem_ThrowItemExceptionInvalidRequest_WhenStartTimeIsBeforeEnd() {
        Long id = 1L;
        User user = new User();
        ItemRequest request = TestData.testItemRequest(
            id,
            OffsetDateTime.now().plusMinutes(2),
            OffsetDateTime.now().plusMinutes(1)
        );

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        assertThrows(ItemException.InvalidRequest.class, () -> itemService.createItem(request));

        verify(userRepository).findById(id);
        verifyNoInteractions(itemRepository);
        verifyNoInteractions(itemMapper);
    }

    @Test
    void createItem_ThrowItemExceptionInvalidRequest_WhenStartTimeIsAfter3Months() {
        Long id = 1L;
        User user = new User();
        ItemRequest request = TestData.testItemRequest(
            id,
            OffsetDateTime.now().plusMonths(3).plusMinutes(1),
            OffsetDateTime.now().plusMonths(3).plusMinutes(2)
        );

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        assertThrows(ItemException.InvalidRequest.class, () -> itemService.createItem(request));

        verify(userRepository).findById(id);
        verifyNoInteractions(itemRepository);
        verifyNoInteractions(itemMapper);
    }

    @Test
    void createItem_ThrowItemExceptionInvalidRequest_WhenAuctionTakesLongerThanAMonth() {
        Long id = 1L;
        User user = new User();
        ItemRequest request = TestData.testItemRequest(
            id,
            OffsetDateTime.now(),
            OffsetDateTime.now().plusMonths(1).plusMinutes(1)
        );

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        assertThrows(ItemException.InvalidRequest.class, () -> itemService.createItem(request));

        verify(userRepository).findById(id);
        verifyNoInteractions(itemRepository);
        verifyNoInteractions(itemMapper);
    }

    @Test
    void updateItem_UpdateAndReturnDto_WhenValidRequestAndOwner() {
        Long itemId = 1L;
        Long userId = 1L;
        int count = 0;
        Item item = TestData.testItem(itemId, AuctionStatus.ACTIVE);
        ItemUpdateRequest request = TestData.testUpdateItemRequest(
            OffsetDateTime.now(),
            OffsetDateTime.now().plusDays(2)
        );

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bidRepository.countByItemId(itemId)).thenReturn(count);

        itemService.updateItem(itemId, userId, request);

        assertEquals(request.title(),item.getTitle());
        assertEquals(request.startingPrice(), item.getCurrentHighestBid());
        
        verify(itemRepository).findById(itemId);
        verify(bidRepository).countByItemId(itemId);
        verify(itemRepository).save(item);
        verify(itemMapper).toDto(item);
    }

    @Test
    void updateItem_ThrowItemExceptionNotFound_WhenItemDoesNotExist() {
        Long itemId = 1L;
        ItemUpdateRequest request = TestData.testUpdateItemRequest(
            OffsetDateTime.now(),
            OffsetDateTime.now().plusDays(2)
        );

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        ItemException.NotFound ex = assertThrows(ItemException.NotFound.class, () -> itemService.updateItem(itemId, 99L, request));
        assertTrue(ex.getMessage().contains(itemId.toString()));
        
        verifyNoInteractions(bidRepository);
        verify(itemRepository, never()).save(any());
    }

    @Test
    void updateItem_ThrowInvalidState_WhenBidsExist() {
        Long itemId = 1L;
        Long userId = 1L;
        int count = 1;
        Item existingItem = TestData.testItem(itemId, AuctionStatus.ACTIVE);
        ItemUpdateRequest request = TestData.testUpdateItemRequest(
            OffsetDateTime.now(),
            OffsetDateTime.now().plusDays(2)
        );

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(bidRepository.countByItemId(itemId)).thenReturn(count);

        assertThrows(ItemException.InvalidState.class, () -> itemService.updateItem(itemId, userId, request));

        verify(bidRepository).countByItemId(itemId);
        verify(itemRepository, never()).save(any());
    }

    @Test
    void updateItem_ThrowItemExceptionInvalidRequest_WhenStartTimeIsBeforeEnd() {
        Long id = 1L;
        Long itemId = 1L;
        Item item = new Item();
        ItemUpdateRequest request = TestData.testUpdateItemRequest(
            OffsetDateTime.now().plusMinutes(2),
            OffsetDateTime.now().plusMinutes(1)
        );

        when(itemRepository.findById(id)).thenReturn(Optional.of(item));
        when(bidRepository.countByItemId(itemId)).thenReturn(0);

        assertThrows(ItemException.InvalidRequest.class, () -> itemService.updateItem(id, itemId, request));

        verify(itemRepository).findById(id);
        verify(bidRepository).countByItemId(itemId);
        verify(itemRepository, never()).save(any());
        verifyNoInteractions(itemMapper);
    }

    @Test
    void updateItem_ThrowItemExceptionInvalidRequest_WhenStartTimeIsAfter3Months() {
        Long id = 1L;
        Long itemId = 1L;
        Item item = new Item();
        ItemUpdateRequest request = TestData.testUpdateItemRequest(
            OffsetDateTime.now().plusMonths(3).plusMinutes(1),
            OffsetDateTime.now().plusMinutes(1)
        );

        when(itemRepository.findById(id)).thenReturn(Optional.of(item));
        when(bidRepository.countByItemId(itemId)).thenReturn(0);

        assertThrows(ItemException.InvalidRequest.class, () -> itemService.updateItem(id, itemId, request));

        verify(itemRepository).findById(id);
        verify(bidRepository).countByItemId(itemId);
        verify(itemRepository, never()).save(any());
        verifyNoInteractions(itemMapper);
    }

    @Test
    void updateItem_ThrowItemExceptionInvalidRequest_WhenAuctionLongerThanAMonth() {
        Long id = 1L;
        Long itemId = 1L;
        Item item = new Item();
        ItemUpdateRequest request = TestData.testUpdateItemRequest(
            OffsetDateTime.now(),
            OffsetDateTime.now().plusMonths(1).plusMinutes(1)
        );

        when(itemRepository.findById(id)).thenReturn(Optional.of(item));
        when(bidRepository.countByItemId(itemId)).thenReturn(0);

        assertThrows(ItemException.InvalidRequest.class, () -> itemService.updateItem(id, itemId, request));

        verify(itemRepository).findById(id);
        verify(bidRepository).countByItemId(itemId);
        verify(itemRepository, never()).save(any());
        verifyNoInteractions(itemMapper);
    }

    @Test
    void deleteItem_DeleteSuccessfully() {
        Long itemId = 1L;
        Item item = TestData.testItem(itemId, AuctionStatus.PENDING);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        itemService.deleteItem(itemId);

        verify(itemRepository).findById(itemId);
        verify(itemRepository).delete(item);
    }

    @Test
    void deleteItem_ThrowInvalidState_WhenItemNotFound() {
        Long itemId = 1L;
        Item item = TestData.testItem(itemId, AuctionStatus.PENDING);
        item.setBidHistory(List.of(TestData.testBid()));

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(ItemException.NotFound.class, () -> itemService.deleteItem(itemId));

        verify(itemRepository).findById(itemId);
        verify(itemRepository, never()).delete(any());
    }

    @Test
    void deleteItem_ThrowInvalidState_WhenBidsExistInHistory() {
        Long itemId = 1L;
        Item item = TestData.testItem(itemId, AuctionStatus.PENDING);
        item.setBidHistory(List.of(TestData.testBid()));

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(ItemException.InvalidState.class, () -> itemService.deleteItem(itemId));

        verify(itemRepository).findById(itemId);
        verify(itemRepository, never()).delete(any());
    }

    @Test
    void deleteItem_ThrowInvalidState_WhenStatusIsNotPending() {
        Long itemId = 1L;
        Item item = TestData.testItem(itemId, AuctionStatus.ACTIVE);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(ItemException.InvalidState.class, () -> itemService.deleteItem(itemId));

        verify(itemRepository).findById(itemId);
        verify(itemRepository, never()).delete(any());
    }

    @Test
    void startAuction_ReturnNumberOfAffectedAuctions() {
        int count = 1;

        when(itemRepository.startPendingItems(any(OffsetDateTime.class))).thenReturn(count);

        int result = itemService.startAuction();

        assertEquals(count, result);

        verify(itemRepository).startPendingItems(any(OffsetDateTime.class));
    }
}
