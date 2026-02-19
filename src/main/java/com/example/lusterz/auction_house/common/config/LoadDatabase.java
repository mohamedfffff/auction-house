package com.example.lusterz.auction_house.common.config;

import java.math.BigDecimal;

import java.time.OffsetDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.lusterz.auction_house.modules.bid.model.Bid;
import com.example.lusterz.auction_house.modules.bid.repository.BidRepository;
import com.example.lusterz.auction_house.modules.item.model.AuctionStatus;
import com.example.lusterz.auction_house.modules.item.model.Item;
import com.example.lusterz.auction_house.modules.item.repository.ItemRepository;
import com.example.lusterz.auction_house.modules.user.model.User;
import com.example.lusterz.auction_house.modules.user.model.UserRole;
import com.example.lusterz.auction_house.modules.user.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class LoadDatabase {

    private final PasswordEncoder passwordEncoder;

    LoadDatabase(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public CommandLineRunner initialDatabase(UserRepository userRepo, ItemRepository itemRepo, BidRepository bidRepo) {
        if (userRepo.count() > 0) return args -> {};

        return args -> {
            
            User seller1 = createUser(userRepo, "seller1", "seller1@test.com");
            User seller2 = createUser(userRepo, "seller2", "seller2@test.com");
            User seller3 = createUser(userRepo, "seller3", "seller3@test.com");
            User bidder1 = createUser(userRepo, "bidder1", "bidder1@test.com");
            User bidder2 = createUser(userRepo, "bidder2", "bidder2@test.com");
            User bidder3 = createUser(userRepo, "bidder3", "bidder3@test.com");

            Item camera = createItem(itemRepo, seller1, "Vintage Leica M583", "500.00");
            Item watch = createItem(itemRepo, seller2, "Gold Rolex 1970", "1200.00");
            Item vinyl = createItem(itemRepo, seller3, "Signed Beatles Record", "100.00");

            createBid(bidRepo, bidder1, camera, "550.00");
            createBid(bidRepo, bidder2, camera, "600.00");
            createBid(bidRepo, bidder3, camera, "650.00");
            createBid(bidRepo, bidder1, watch, "550.00");
            createBid(bidRepo, bidder2, watch, "600.00");
            createBid(bidRepo, bidder3, vinyl, "650.00");

            log.info("Database loaded with mock data");
        };
    }


    private User createUser(UserRepository repo, String name, String email) {
        User user = new User();
        user.setUsername(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(name)); 
        user.setBalance(new BigDecimal("1000.00"));
        user.setUserImageUrl(email + ".image.com");
        user.setActive(true);
        user.setRole(UserRole.USER);
        return repo.save(user);
    }

    private Item createItem(ItemRepository repo, User seller, String title, String price) {
        Item item = new Item();
        item.setTitle(title);
        item.setSeller(seller);
        item.setDescription("a very good description");
        item.setItemImageUrl(title + ".image.com");
        item.setStartingPrice(new BigDecimal(price));
        item.setCurrentHighestBid(new BigDecimal(price));
        item.setStartTime(OffsetDateTime.now().plusSeconds(5));
        item.setEndTime(OffsetDateTime.now().plusMinutes(1));
        item.setStatus(AuctionStatus.PENDING);
        return repo.save(item);
    }

    private void createBid(BidRepository repo, User bidder, Item item, String amount) {
        Bid bid = new Bid();
        bid.setBidder(bidder);
        bid.setItem(item);
        bid.setAmount(new BigDecimal(amount));
        bid.setBidTime(OffsetDateTime.now());
        repo.save(bid);
    }
}
