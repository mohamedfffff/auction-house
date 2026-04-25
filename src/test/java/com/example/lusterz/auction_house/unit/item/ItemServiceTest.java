package com.example.lusterz.auction_house.unit.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

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
import com.example.lusterz.auction_house.modules.bid.repository.BidRepository;
import com.example.lusterz.auction_house.modules.item.dto.ItemDto;
import com.example.lusterz.auction_house.modules.item.mapper.ItemMapper;
import com.example.lusterz.auction_house.modules.item.model.AuctionStatus;
import com.example.lusterz.auction_house.modules.item.model.Item;
import com.example.lusterz.auction_house.modules.item.repository.ItemRepository;
import com.example.lusterz.auction_house.modules.item.service.ItemService;
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
}
