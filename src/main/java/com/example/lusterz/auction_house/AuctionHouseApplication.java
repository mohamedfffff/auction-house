package com.example.lusterz.auction_house;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@EnableScheduling
public class AuctionHouseApplication {
	public static void main(String[] args) {
		SpringApplication.run(AuctionHouseApplication.class, args);
		log.info("Server started at port 8080");
	}
}
