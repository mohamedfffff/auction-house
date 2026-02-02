package com.example.lusterz.auction_house.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.lusterz.auction_house.model.enums.AuctionStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Version;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
public class AuctionItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 2, max = 100)
    private String title;

    @NotBlank
    private String description;
    private String itemImageUrl;

    @DecimalMin(value = "0.01")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal startingPrice;

    @Positive
    @Digits(integer = 10, fraction = 2)
    private BigDecimal currentHighestBid;

    @NotNull
    @FutureOrPresent
    private LocalDateTime startTime;

    @NotNull
    @FutureOrPresent
    private LocalDateTime endTime;

    private AuctionStatus status = AuctionStatus.ACTIVE;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User seller;

    @ManyToOne
    @JoinColumn(name = "winner_id")
    private User currentWinner;

    @OneToMany(mappedBy = "auctionItem", cascade = CascadeType.ALL)
    private List<Bid> bidHistory;

}
