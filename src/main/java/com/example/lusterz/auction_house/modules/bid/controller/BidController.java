package com.example.lusterz.auction_house.modules.bid.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.lusterz.auction_house.modules.bid.dto.BidDto;
import com.example.lusterz.auction_house.modules.bid.dto.BidRequest;
import com.example.lusterz.auction_house.modules.bid.service.BidService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("api/v1/bids")
public class BidController {

    private final BidService bidService;

    public BidController(BidService bidService){
        this.bidService = bidService;
    }
    
    @GetMapping("/{id}")
    public BidDto getBid(@PathVariable Long id) {
        return bidService.getBid(id);
    }
    
    @GetMapping
    public List<BidDto> getAllBids(@RequestParam(required = false) Long bidderId, @RequestParam(required = false) Long item_id) {
        if (bidderId != null) {
            return bidService.getAllBidsByBidderId(bidderId);
        } 

        if (item_id != null) {
            return bidService.getAllBidsByItemId(item_id);
        }

        return bidService.getAllBids();
    }

    @PostMapping//to-do pull bidder id from security
    public ResponseEntity<BidDto> placeBid(@RequestParam Long bidderId, @RequestParam Long itemId, @RequestBody BidRequest bidRequest) {
        BidDto bid = bidService.placeBid(bidderId, itemId, bidRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(bid);
    }

    @DeleteMapping//to-do pull bidder id from security
    public ResponseEntity<Void> deleteBid(@RequestParam Long bidderId, @RequestParam Long itemId, @RequestParam Long bidId) {
        bidService.deleteBid(bidderId, itemId, bidId);
        return ResponseEntity.noContent().build();
    }
}
