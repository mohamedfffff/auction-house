package com.example.lusterz.auction_house.modules.user.model;

import java.util.ArrayList;
import java.util.List;

import com.example.lusterz.auction_house.modules.bid.model.Bid;
import com.example.lusterz.auction_house.modules.item.model.Item;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 2, max = 100)
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank
    @Email
    @Column(unique = true, nullable = false)
    private String email;
    
    private String profileImage;

    @NotNull
    @Column(nullable = false)
    private boolean active = false;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.ROLE_USER;

    // delete sub user credentials when user is deleted
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserCredential> userCredentials = new ArrayList<>();

    @OneToMany(mappedBy = "bidder")
    private List<Bid> userBids = new ArrayList<>();

    @OneToMany(mappedBy = "seller")
    private List<Item> itemsForSale = new ArrayList<>();
}
