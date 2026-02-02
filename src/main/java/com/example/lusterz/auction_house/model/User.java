package com.example.lusterz.auction_house.model;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity(name = "users")
@Data
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 2, max = 100)
    @Column(unique = true)
    private String username;

    @NotBlank
    @Email
    @Column(unique = true)
    private String email;

    @NotBlank
    private String password;
    
    private String userImageUrl;

    @Positive
    private BigDecimal balance;

    @OneToMany(mappedBy = "bidder")
    private List<Bid> userBids;

    @OneToMany(mappedBy = "seller")
    private List<AuctionItem> itemsForSale;
}
