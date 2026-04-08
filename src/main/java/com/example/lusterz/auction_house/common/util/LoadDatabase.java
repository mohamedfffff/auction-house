package com.example.lusterz.auction_house.common.util;

import java.math.BigDecimal;

import java.time.OffsetDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.lusterz.auction_house.modules.auth.model.AuthProviders;
import com.example.lusterz.auction_house.modules.bid.model.Bid;
import com.example.lusterz.auction_house.modules.bid.repository.BidRepository;
import com.example.lusterz.auction_house.modules.item.model.AuctionStatus;
import com.example.lusterz.auction_house.modules.item.model.Item;
import com.example.lusterz.auction_house.modules.item.repository.ItemRepository;
import com.example.lusterz.auction_house.modules.user.model.User;
import com.example.lusterz.auction_house.modules.user.model.UserCredential;
import com.example.lusterz.auction_house.modules.user.model.UserRole;
import com.example.lusterz.auction_house.modules.user.repository.UserCredentialRepository;
import com.example.lusterz.auction_house.modules.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Configuration
public class LoadDatabase {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserCredentialRepository userCredentialRepository;
    private final ItemRepository itemRepository;
    private final BidRepository bidRepository;
    private final MailSender mailSender;

    @Bean
    public CommandLineRunner initialDatabase() {
        if (userRepository.count() > 0) return args -> {};

        return args -> {
            
            User seller1 = createUser("seller1", "seller1@test.com");
            User seller2 = createUser("seller2", "seller2@test.com");
            User bidder1 = createUser("bidder1", "bidder1@test.com");
            User bidder2 = createUser("bidder2", "bidder2@test.com");

            Item camera = createItem(seller1, "Vintage Leica M583", "500.00");
            createItem(seller2, "Bmw car", "61441215");

            createBid(bidder1, camera, "550.00");
            createBid(bidder2, camera, "600.00");

            log.info("Database loaded with mock data");

            // sendTestEmail();
            
            
        };
    }


    private User createUser(String name, String email) {
        User user = new User();
        user.setUsername(name);
        user.setEmail(email);
        user.setProfileImage(email + ".image.com");
        user.setActive(true);
        user.setRole(UserRole.ROLE_USER);
        userRepository.save(user);

        UserCredential newUserCredential = new UserCredential();
        newUserCredential.setUser(user);
        newUserCredential.setProvider(AuthProviders.LOCAL);
        newUserCredential.setPassword(passwordEncoder.encode(name + "password"));
        userCredentialRepository.save(newUserCredential);

        return user;
    }

    private Item createItem(User seller, String title, String price) {
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
        return itemRepository.save(item);
    }

    private void createBid(User bidder, Item item, String amount) {
        Bid bid = new Bid();
        bid.setBidder(bidder);
        bid.setItem(item);
        bid.setAmount(new BigDecimal(amount));
        bid.setBidTime(OffsetDateTime.now().plusSeconds(10));
        bidRepository.save(bid);
    }

    private void sendTestEmail() {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom("test@auction-house.com");
            msg.setTo("check-this@mailtrap.io");
            msg.setSubject("Direct Test");
            msg.setText("Testing the connection...");
            mailSender.send(msg);
            log.info("test email sent");
        } catch (Exception e) {
            log.error("test email failed {}", e);
        }
    }
}
