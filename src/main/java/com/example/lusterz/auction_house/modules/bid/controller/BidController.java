package com.example.lusterz.auction_house.modules.bid.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.lusterz.auction_house.modules.bid.dto.BidDto;
import com.example.lusterz.auction_house.modules.bid.dto.BidRequest;
import com.example.lusterz.auction_house.modules.bid.service.BidService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/bids")
public class BidController {

    private final BidService bidService;

    @GetMapping("/{id}")
    public BidDto getBid(@PathVariable Long id) {
        return bidService.getBid(id);
    }
    
    @GetMapping
    public List<BidDto> getAllBids(@RequestParam(required = false) Long bidderId, @RequestParam(required = false) Long itemId) {
        if (bidderId != null) {
            return bidService.getAllBidsByBidderId(bidderId);
        } 

        if (itemId != null) {
            return bidService.getAllBidsByItemId(itemId);
        }

        return bidService.getAllBids();
    }

    @PostMapping
    public ResponseEntity<BidDto> placeBid(@Valid @RequestBody BidRequest bidRequest) {
        BidDto bid = bidService.placeBid(bidRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(bid);
    }

    @DeleteMapping("/{bidId}")
    public ResponseEntity<Void> deleteBid(@PathVariable Long bidId, @RequestParam Long bidderId, @RequestParam Long itemId) {
        bidService.deleteBid(bidderId, itemId, bidId);
        return ResponseEntity.noContent().build();
    }
}
