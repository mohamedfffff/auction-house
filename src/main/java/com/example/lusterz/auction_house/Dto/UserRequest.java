package com.example.lusterz.auction_house.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRequest {
    
    private String username;
    private String email;
    private String password;
    private String userImageUrl;

}
