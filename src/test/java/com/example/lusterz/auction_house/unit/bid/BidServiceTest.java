package com.example.lusterz.auction_house.unit.bid;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
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
import com.example.lusterz.auction_house.modules.bid.dto.BidRequest;
import com.example.lusterz.auction_house.modules.bid.mapper.BidMapper;
import com.example.lusterz.auction_house.modules.bid.model.Bid;
import com.example.lusterz.auction_house.modules.bid.repository.BidRepository;
import com.example.lusterz.auction_house.modules.bid.service.BidService;
import com.example.lusterz.auction_house.modules.item.model.AuctionStatus;
import com.example.lusterz.auction_house.modules.item.model.Item;
import com.example.lusterz.auction_house.modules.item.repository.ItemRepository;
import com.example.lusterz.auction_house.modules.user.model.User;
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

    @Test
    void placeBid_SaveBidAndReturnDto_WhenValidRequest() {
        Long id = 1L;
        User seller = TestData.testUser(2L, false);
        User bidder = TestData.testUser(id, false);
        Item item = TestData.testItem(id, AuctionStatus.ACTIVE);
        item.setSeller(seller);
        BidRequest request = new BidRequest(BigDecimal.TEN, id, id);
 
        when(userRepository.findById(id)).thenReturn(Optional.of(bidder));
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));
 
        BidDto result = bidService.placeBid(request);
 
        assertEquals(request.amount(), result.amount());
        assertEquals(request.amount(), item.getCurrentHighestBid());
 
        verify(userRepository).findById(id);
        verify(itemRepository).findById(id);
        verify(bidRepository).save(any(Bid.class));
        verify(bidMapper).toDto(any(Bid.class));
    }
 
    @Test
    void placeBid_ThrowUserExceptionNotFound_WhenBidderNotFound() {
        Long id = 1L;
        BidRequest request = new BidRequest(BigDecimal.TEN, id, id);
 
        when(userRepository.findById(id)).thenReturn(Optional.empty());
 
        UserException.NotFound ex = assertThrows(UserException.NotFound.class, () -> bidService.placeBid(request));
        assertTrue(ex.getMessage().contains(id.toString()));
 
        verify(userRepository).findById(id);
        verifyNoInteractions(itemRepository);
        verifyNoInteractions(bidRepository);
        verifyNoInteractions(bidMapper);
    }
 
    @Test
    void placeBid_ThrowItemExceptionNotFound_WhenItemNotFound() {
        Long id = 1L;
        User bidder = TestData.testUser(id, false);
        BidRequest request = new BidRequest(BigDecimal.TEN, id, id);
 
        when(userRepository.findById(id)).thenReturn(Optional.of(bidder));
        when(itemRepository.findById(id)).thenReturn(Optional.empty());
 
        ItemException.NotFound ex = assertThrows(ItemException.NotFound.class, () -> bidService.placeBid(request));
        assertTrue(ex.getMessage().contains(id.toString()));
 
        verify(userRepository).findById(id);
        verify(itemRepository).findById(id);
        verifyNoInteractions(bidRepository);
        verifyNoInteractions(bidMapper);
    }
 
    @Test
    void placeBid_ThrowBidExceptionUnauthorized_WhenBidderIsItemOwner() {
        Long id = 1L;
        User seller = TestData.testUser(id, false);
        Item item = TestData.testItem(id, AuctionStatus.ACTIVE);
        item.setSeller(seller);
        BidRequest request = new BidRequest(BigDecimal.TEN, id, id);
 
        when(userRepository.findById(id)).thenReturn(Optional.of(seller));
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));
 
        assertThrows(BidException.Unauthorized.class, () -> bidService.placeBid(request));
 
        verify(userRepository).findById(id);
        verify(itemRepository).findById(id);
        verifyNoInteractions(bidRepository);
        verifyNoInteractions(bidMapper);
    }
 
    @Test
    void placeBid_ThrowItemExceptionInvalidState_WhenItemIsNotActive() {
        Long id = 1L;
        User bidder = TestData.testUser(id, false);
        User seller = TestData.testUser(2L, false);
        Item item = TestData.testItem(id, AuctionStatus.PENDING);
        item.setSeller(seller);
        BidRequest request = new BidRequest(BigDecimal.TEN, id, id);
 
        when(userRepository.findById(id)).thenReturn(Optional.of(bidder));
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));
 
        assertThrows(ItemException.InvalidState.class, () -> bidService.placeBid(request));
 
        verify(userRepository).findById(id);
        verify(itemRepository).findById(id);
        verifyNoInteractions(bidRepository);
        verifyNoInteractions(bidMapper);
    }
 
    @Test
    void placeBid_ThrowBidExceptionInsufficientBid_WhenAmountLessThanCurrentHighest() {
        Long id = 1L;
        User bidder = TestData.testUser(id, false);
        User seller = TestData.testUser(2L, false);
        Item item = TestData.testItem(id, AuctionStatus.ACTIVE);
        item.setSeller(seller);
        BidRequest request = new BidRequest(BigDecimal.ONE, id, id);
 
        when(userRepository.findById(id)).thenReturn(Optional.of(bidder));
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));
 
        assertThrows(BidException.InsufficientBid.class, () -> bidService.placeBid(request));
 
        verify(userRepository).findById(id);
        verify(itemRepository).findById(id);
        verifyNoInteractions(bidRepository);
        verifyNoInteractions(bidMapper);
    }
 
    @Test
    void deleteBid_DeleteBid_WhenBidIsNotHighest() {
        Long id = 1L;
        Item item = TestData.testItem(id, AuctionStatus.ACTIVE);
        Bid bid = TestData.testBid(id, BigDecimal.ONE);
 
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));
        when(bidRepository.findById(id)).thenReturn(Optional.of(bid));
 
        bidService.deleteBid(id, id, id);
 
        verify(itemRepository).findById(id);
        verify(bidRepository).findById(id);
        verify(bidRepository).delete(bid);
        verify(bidRepository, never()).findTopByItemOrderByAmountDesc(item);
    }
 
    @Test
    void deleteBid_DeleteBidAndUpdateHighestBid_WhenBidIsHighestAndOtherBidsExist() {
        Long id = 1L;
        Item item = TestData.testItem(id, AuctionStatus.ACTIVE);
        Bid bid = TestData.testBid(id);
        Bid nextHighest = TestData.testBid(2L, BigDecimal.ONE);
 
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));
        when(bidRepository.findById(id)).thenReturn(Optional.of(bid));
        when(bidRepository.findTopByItemOrderByAmountDesc(item)).thenReturn(Optional.of(nextHighest));
 
        bidService.deleteBid(id, id, id);
 
        assertEquals(nextHighest.getAmount(), item.getCurrentHighestBid());
 
        verify(itemRepository).findById(id);
        verify(bidRepository).findById(id);
        verify(bidRepository).delete(bid);
        verify(bidRepository).findTopByItemOrderByAmountDesc(item);
    }
 
    @Test
    void deleteBid_DeleteBidAndResetToStartingPrice_WhenBidIsHighestAndNoBidsRemain() {
        Long id = 1L;
        Item item = TestData.testItem(id, AuctionStatus.ACTIVE);
        Bid bid = TestData.testBid(id);
 
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));
        when(bidRepository.findById(id)).thenReturn(Optional.of(bid));
        when(bidRepository.findTopByItemOrderByAmountDesc(item)).thenReturn(Optional.empty());
 
        bidService.deleteBid(id, id, id);
 
        assertEquals(item.getStartingPrice(), item.getCurrentHighestBid());
 
        verify(itemRepository).findById(id);
        verify(bidRepository).findById(id);
        verify(bidRepository).delete(bid);
        verify(bidRepository).findTopByItemOrderByAmountDesc(item);
    }
 
    @Test
    void deleteBid_ThrowItemExceptionNotFound_WhenItemNotFound() {
        Long id = 1L;
 
        when(itemRepository.findById(id)).thenReturn(Optional.empty());
 
        ItemException.NotFound ex = assertThrows(ItemException.NotFound.class, () -> bidService.deleteBid(id, id, id));
        assertTrue(ex.getMessage().contains(id.toString()));
 
        verify(itemRepository).findById(id);
        verifyNoInteractions(bidRepository);
    }
 
    @Test
    void deleteBid_ThrowBidExceptionNotFound_WhenBidNotFound() {
        Long id = 1L;
        Item item = TestData.testItem(id, AuctionStatus.ACTIVE);
 
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));
        when(bidRepository.findById(id)).thenReturn(Optional.empty());
 
        BidException.NotFound ex = assertThrows(BidException.NotFound.class, () -> bidService.deleteBid(id, id, id));
        assertTrue(ex.getMessage().contains(id.toString()));
 
        verify(itemRepository).findById(id);
        verify(bidRepository).findById(id);
        verify(bidRepository, never()).delete(any());
    }
 
    @Test
    void deleteBid_ThrowItemExceptionInvalidState_WhenItemIsNotActive() {
        Long id = 1L;
        Item item = TestData.testItem(id, AuctionStatus.CLOSED);
        Bid bid = TestData.testBid(id);
 
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));
        when(bidRepository.findById(id)).thenReturn(Optional.of(bid));
 
        assertThrows(ItemException.InvalidState.class, () -> bidService.deleteBid(id, id, id));
 
        verify(itemRepository).findById(id);
        verify(bidRepository).findById(id);
        verify(bidRepository, never()).delete(any());
    }
}
