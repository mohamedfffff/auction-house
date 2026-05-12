package com.example.lusterz.auction_house.unit.bid;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

import com.example.lusterz.auction_house.TestData;
import com.example.lusterz.auction_house.common.exception.BidException;
import com.example.lusterz.auction_house.common.exception.ItemException;
import com.example.lusterz.auction_house.common.exception.UserException;
import com.example.lusterz.auction_house.modules.bid.dto.BidDto;
import com.example.lusterz.auction_house.modules.bid.mapper.BidMapper;
import com.example.lusterz.auction_house.modules.bid.model.Bid;
import com.example.lusterz.auction_house.modules.bid.repository.BidRepository;
import com.example.lusterz.auction_house.modules.bid.service.BidService;
import com.example.lusterz.auction_house.modules.item.repository.ItemRepository;
import com.example.lusterz.auction_house.modules.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class BidServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private ItemRepository itemRepository;
    @Mock private BidRepository bidRepository;
    
    @Spy
    private BidMapper bidMapper = Mappers.getMapper(BidMapper.class) ;

    @InjectMocks
    private BidService bidService;

    @Test
    void getBid_ReturnBidDto_WhenFound() {
        Long id = 1L;
        Bid bid = TestData.testBid(id);
        BidDto dto = TestData.testBidDto(bid);
 
        when(bidRepository.findById(id)).thenReturn(Optional.of(bid));
 
        BidDto result = bidService.getBid(id);
 
        assertEquals(dto, result);
 
        verify(bidRepository).findById(id);
        verify(bidMapper).toDto(bid);
    }
 
    @Test
    void getBid_ThrowBidExceptionNotFound_WhenNotFound() {
        Long id = 1L;
 
        when(bidRepository.findById(id)).thenReturn(Optional.empty());
 
        BidException.NotFound ex = assertThrows(BidException.NotFound.class, () -> bidService.getBid(id));
        assertTrue(ex.getMessage().contains(id.toString()));
 
        verify(bidRepository).findById(id);
        verifyNoInteractions(bidMapper);
    }

    @Test
    void getAllBids_ReturnBidDtoList_WhenBidsExist() {
        Bid bid = TestData.testBid(1L);
        BidDto dto = TestData.testBidDto(bid);
 
        when(bidRepository.findAll()).thenReturn(List.of(bid));
 
        List<BidDto> result = bidService.getAllBids();
 
        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
 
        verify(bidRepository).findAll();
        verify(bidMapper).toDto(bid);
    }
 
    @Test
    void getAllBids_ReturnEmptyList_WhenNoBidsExist() {
        when(bidRepository.findAll()).thenReturn(List.of());
 
        List<BidDto> result = bidService.getAllBids();
 
        assertTrue(result.isEmpty());
 
        verify(bidRepository).findAll();
        verifyNoInteractions(bidMapper);
    }
 
    @Test
    void getAllBidsByBidderId_ReturnBidDtoList_WhenBidderExistsAndHasBids() {
        Long id = 1L;
        Bid bid = TestData.testBid(1L);
        BidDto dto = TestData.testBidDto(bid);
 
        when(userRepository.existsById(id)).thenReturn(true);
        when(bidRepository.findAllByBidderId(id)).thenReturn(List.of(bid));
 
        List<BidDto> result = bidService.getAllBidsByBidderId(id);
 
        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
 
        verify(userRepository).existsById(id);
        verify(bidRepository).findAllByBidderId(id);
        verify(bidMapper).toDto(bid);
    }
 
    @Test
    void getAllBidsByBidderId_ReturnEmptyList_WhenBidderExistsAndHasNoBids() {
        Long id = 1L;
 
        when(userRepository.existsById(id)).thenReturn(true);
        when(bidRepository.findAllByBidderId(id)).thenReturn(List.of());
 
        List<BidDto> result = bidService.getAllBidsByBidderId(id);
 
        assertTrue(result.isEmpty());
 
        verify(userRepository).existsById(id);
        verify(bidRepository).findAllByBidderId(id);
        verifyNoInteractions(bidMapper);
    }
 
    @Test
    void getAllBidsByBidderId_ThrowUserExceptionNotFound_WhenBidderNotFound() {
        Long id = 1L;
 
        when(userRepository.existsById(id)).thenReturn(false);
 
        UserException.NotFound ex = assertThrows(UserException.NotFound.class, () -> bidService.getAllBidsByBidderId(id));
        assertTrue(ex.getMessage().contains(id.toString()));
 
        verify(userRepository).existsById(id);
        verifyNoInteractions(bidRepository);
        verifyNoInteractions(bidMapper);
    }
 
    @Test
    void getAllBidsByItemId_ReturnBidDtoList_WhenItemExistsAndHasBids() {
        Long id = 1L;
        Bid bid = TestData.testBid(1L);
        BidDto dto = TestData.testBidDto(bid);
 
        when(itemRepository.existsById(id)).thenReturn(true);
        when(bidRepository.findAllByItemId(id)).thenReturn(List.of(bid));
 
        List<BidDto> result = bidService.getAllBidsByItemId(id);
 
        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
 
        verify(itemRepository).existsById(id);
        verify(bidRepository).findAllByItemId(id);
        verify(bidMapper).toDto(bid);
    }
 
    @Test
    void getAllBidsByItemId_ReturnEmptyList_WhenItemExistsAndHasNoBids() {
        Long id = 1L;
 
        when(itemRepository.existsById(id)).thenReturn(true);
        when(bidRepository.findAllByItemId(id)).thenReturn(List.of());
 
        List<BidDto> result = bidService.getAllBidsByItemId(id);
 
        assertTrue(result.isEmpty());
 
        verify(itemRepository).existsById(id);
        verify(bidRepository).findAllByItemId(id);
        verifyNoInteractions(bidMapper);
    }
 
    @Test
    void getAllBidsByItemId_ThrowItemExceptionNotFound_WhenItemNotFound() {
        Long id = 1L;
 
        when(itemRepository.existsById(id)).thenReturn(false);
 
        ItemException.NotFound ex = assertThrows(ItemException.NotFound.class, () -> bidService.getAllBidsByItemId(id));
        assertTrue(ex.getMessage().contains(id.toString()));
 
        verify(itemRepository).existsById(id);
        verifyNoInteractions(bidRepository);
        verifyNoInteractions(bidMapper);
    }

}
