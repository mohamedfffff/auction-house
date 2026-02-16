package com.example.lusterz.auction_house.common.config;

import java.math.BigDecimal;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.lusterz.auction_house.modules.bid.model.Bid;
import com.example.lusterz.auction_house.modules.bid.repository.BidRepository;
import com.example.lusterz.auction_house.modules.item.model.Item;
import com.example.lusterz.auction_house.modules.item.repository.ItemRepository;
import com.example.lusterz.auction_house.modules.user.model.User;
import com.example.lusterz.auction_house.modules.user.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class LoadDatabase {

    @Bean
    public CommandLineRunner intialDatabase(UserRepository userRepository, ItemRepository auctionItemRepository, BidRepository bidRepository) {
        if (userRepository.count() > 0) return args ->{};
        return args -> {

            User seller = new User();
            seller.setUsername("ArtCollector9999");
            seller.setEmail("seller@example.com");
            seller.setPassword("hashed_pass_1");
            seller.setBalance(new BigDecimal(5841));
            seller.setUserImageUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSOOOp7Ae6JdqU8o-6BLyjvrep4SEd8mfKx2w&s");
            userRepository.save(seller);
            log.info("seller created");

            User bidder = new User();
            bidder.setUsername("LuuuckyBidder");
            bidder.setEmail("bidder@example.com");
            bidder.setPassword("hashed_pass_2");
            bidder.setBalance(new BigDecimal(541));
            bidder.setUserImageUrl("https://img.freepik.com/free-vector/blue-circle-with-white-user_78370-4707.jpg?semt=ais_hybrid&w=740&q=80");
            userRepository.save(bidder);
            log.info("seller created");

            Item item = new Item();
            item.setTitle("Vintage Leica M583 Camera");
            item.setDescription("Excellent condition, original leather case included.");
            item.setItemImageUrl("https://leaders.jo/wp-content/uploads/2025/09/image-24-large.png");
            item.setStartingPrice(new BigDecimal("500.00"));
            item.setCurrentHighestBid(new BigDecimal("550.00"));
            item.setStartTime(LocalDateTime.now().plusMinutes(1));
            item.setEndTime(LocalDateTime.now().plusDays(7)); 
            item.setSeller(seller);
            item.setWinner(bidder); 
            auctionItemRepository.save(item);
            log.info("item created");

            Bid firstBid = new Bid();
            firstBid.setAmount(new BigDecimal("50.00"));
            firstBid.setBidTime(LocalDateTime.now());
            firstBid.setBidder(bidder);
            firstBid.setItem(item);
            bidRepository.save(firstBid);
            log.info("bid created");
            
            log.info("database initialized with Sample Data!");
        };
    }
}
