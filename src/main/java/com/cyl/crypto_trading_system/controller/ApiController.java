package com.cyl.crypto_trading_system.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cyl.crypto_trading_system.DataInitializer;
import com.cyl.crypto_trading_system.model.*;
import com.cyl.crypto_trading_system.service.*;

@RestController
@RequestMapping("/api")
public class ApiController {

	private final WalletService walletService;
	private final TransactionService transactionService;
	private final UserService userService;
	private final DataInitializer dataInitializer;
	private final PriceAggregatorService priceAggregatorService;

	@Autowired
	public ApiController(WalletService walletService, TransactionService transactionService, UserService userService, DataInitializer dataInitializer, PriceAggregatorService priceAggregatorService) {
	    this.walletService = walletService;
	    this.transactionService = transactionService;
	    this.userService = userService;
		this.dataInitializer = dataInitializer;
		this.priceAggregatorService = priceAggregatorService;
	}
	
	@GetMapping("/user")
	public Map<String, Object> getUsername() {
	    String defaultUsername = dataInitializer.getDefaultUser().getUsername();
	    return Map.of(
	            "username", defaultUsername
	        );
	}

    @GetMapping("/wallet")
    public Map<String, Object> getWalletBalance() {
    	User defaultUser = dataInitializer.getDefaultUser();
    	Wallet wallet = walletService.getWallet(defaultUser);
    	BigDecimal btc = walletService.getCryptoCurrency("BTC", wallet.getBtcAmount());
    	BigDecimal eth = walletService.getCryptoCurrency("ETH", wallet.getEthAmount());
    	return Map.of(
    		"total", wallet.getUsdtBalance().add(btc).add(eth),
            "usdt", wallet.getUsdtBalance(),
            "btc", btc,
            "eth", eth
        );
    }

    @GetMapping("/prices")
    public Map<String, Object> getLatestPrices() {
    	BigDecimal btcBestBid = priceAggregatorService.getBtcBestBidPrice();
        BigDecimal btcBestAsk = priceAggregatorService.getBtcBestAskPrice();
        BigDecimal ethBestBid = priceAggregatorService.getEthBestBidPrice();
        BigDecimal ethBestAsk = priceAggregatorService.getEthBestAskPrice();
        return Map.of(
            "btcBid", btcBestBid,
            "btcAsk", btcBestAsk,
            "ethBid", ethBestBid,
            "ethAsk", ethBestAsk
        );
    }
    
    @GetMapping("/history")
    public List<Transaction> getTradingHistory() {
        return transactionService.getUserTransactions(dataInitializer.getDefaultUser());
    }
    
    @PostMapping("/trade")
    public ResponseEntity<String> submitTrade(@RequestBody TradeRequest tradeRequest) {
    	Wallet wallet = walletService.getWallet(dataInitializer.getDefaultUser());
        BigDecimal amount = tradeRequest.getAmount();
        String pair = tradeRequest.getPair();
        String type = tradeRequest.getType();
        
        BigDecimal price = priceAggregatorService.getPriceForPair(pair, type);
        
        if (price == null) {
            return ResponseEntity.badRequest().body("Invalid pair");
        }
        
        if (type.equals("BUY")) {
            if (!walletService.hasEnoughBalanceForBuy(wallet, pair, amount, price)) {
                return ResponseEntity.badRequest().body("Insufficient USDT balance");
            }
        } else if (type.equals("SELL")) {
            if (!walletService.hasEnoughBalanceForSell(wallet, pair, amount)) {
                return ResponseEntity.badRequest().body("Insufficient cryptocurrency balance");
            }
        }
        
        transactionService.executeTrade(pair, type, amount, price, dataInitializer.getDefaultUser());

        walletService.updateWallet(wallet, pair, type, amount, price);

        return ResponseEntity.ok("Trade executed successfully!");
    }
    
}
