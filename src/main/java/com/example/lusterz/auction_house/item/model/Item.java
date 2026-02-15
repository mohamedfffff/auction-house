package com.example.lusterz.auction_house.item.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.lusterz.auction_house.bid.model.Bid;
import com.example.lusterz.auction_house.user.model.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(min = 2, max = 100)
    private String title;

    @NotBlank
    private String description;
    private String itemImageUrl;

    @NotNull
    @DecimalMin(value = "0.01")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal startingPrice;

    @NotNull
    private BigDecimal currentHighestBid;

    @NotNull
    @FutureOrPresent
    private LocalDateTime startTime;

    @NotNull
    @Future
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private AuctionStatus status = AuctionStatus.PENDING;

    @Version
    private Integer version;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User seller;

    @ManyToOne
    @JoinColumn(name = "winner_id")
    private User winner;

    @OneToMany(mappedBy = "item")
    private List<Bid> bidHistory = new ArrayList<>();

}
