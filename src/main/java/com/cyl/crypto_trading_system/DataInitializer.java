package com.cyl.crypto_trading_system;

import com.cyl.crypto_trading_system.model.*;
import com.cyl.crypto_trading_system.service.*;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

	private final UserService userService;
	private final WalletService walletService;

    public DataInitializer(UserService userService, WalletService walletService) {
        this.userService = userService;
		this.walletService = walletService;
    }

    @Override
    public void run(String... args) throws Exception {
    	String username = "john";
    	String password = "12345";
        BigDecimal usdtBalance = new BigDecimal(50000);
        BigDecimal ethAmount = new BigDecimal(0);
        BigDecimal btcAmount = new BigDecimal(0);
        if (!userService.userExists(username)) {
        	User savedUser = userService.createUser(username, password);
            System.out.println("Sample user1 created: " + savedUser.getId() + "-" + savedUser.getUsername());
            walletService.createWallet(usdtBalance, ethAmount, btcAmount, savedUser);
            System.out.println("Wallet created for user1: " + savedUser.getId() + "-" + savedUser.getUsername());
        } else {
            System.out.println("User already exists: " + username);
        }
        
        String username2 = "sharon";
    	String password2 = "54321";
        BigDecimal usdtBalance2 = new BigDecimal(50000);
        BigDecimal ethAmount2 = new BigDecimal(0);
        BigDecimal btcAmount2 = new BigDecimal(0);
        if (!userService.userExists(username2)) {
        	User savedUser2 = userService.createUser(username2, password2);
            System.out.println("Sample user2 created: " + savedUser2.getId() + "-" + savedUser2.getUsername());
            walletService.createWallet(usdtBalance2, ethAmount2, btcAmount2, savedUser2);
            System.out.println("Wallet created for user2: " + savedUser2.getId() + "-" + savedUser2.getUsername());
        } else {
            System.out.println("User already exists: " + username);
        }
    }
    
    public User getDefaultUser() {
        return userService.getUser((long) 2);
    }
    
}
