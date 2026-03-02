package com.example.lusterz.auction_house.infrastructure.notification;

import java.math.BigDecimal;

public interface EmailService {
    
    void sendWinnerEmail(String email, String item, BigDecimal price);

    void sendSellerEmail(String email, String item, String winner, BigDecimal price);

    void sendExpiredEmail(String email, String item);
}
