package com.cyl.crypto_trading_system.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cyl.crypto_trading_system.model.User;
import com.cyl.crypto_trading_system.model.Wallet;
import com.cyl.crypto_trading_system.repository.WalletRepository;

@Service
public class WalletService {

	private final WalletRepository walletRepository;
	private final PriceAggregatorService priceAggregatorService;

    @Autowired
    public WalletService(WalletRepository walletRepository, PriceAggregatorService priceAggregatorService) {
		this.walletRepository = walletRepository;
		this.priceAggregatorService = priceAggregatorService;
    }
    
	public Wallet createWallet(BigDecimal usdtBalance, BigDecimal ethAmount, BigDecimal btcAmount, User user) {
		Wallet wallet = new Wallet();
        wallet.setUsdtBalance(usdtBalance);
        wallet.setEthAmount(ethAmount);
        wallet.setBtcAmount(btcAmount);
        wallet.setUser(user);
        return walletRepository.save(wallet);
	}
	
	public Wallet getWallet(User user) {
        return walletRepository.findByUser(user);
	}

	public BigDecimal getCryptoCurrency(String crypto, BigDecimal amount) {
		BigDecimal bestBidPrice = BigDecimal.ZERO;
		if (crypto.equals("BTC")) {
			bestBidPrice = priceAggregatorService.getBtcBestBidPrice(); 
		}
		else if (crypto.equals("ETH")) {
			bestBidPrice = priceAggregatorService.getEthBestBidPrice(); 
		}
		if (amount != null && bestBidPrice != null) {
		    return amount.multiply(bestBidPrice);
		} 
		return BigDecimal.ZERO;
	}

	public boolean hasEnoughBalanceForBuy(Wallet wallet, String pair, BigDecimal amount, BigDecimal price) {
		if (pair.equals("BTC/USDT") || pair.equals("ETH/USDT")) {
            BigDecimal totalCost = amount.multiply(price); // Calculate total cost for the buy order
            return wallet.getUsdtBalance().compareTo(totalCost) >= 0;
        }
        return false;
	}

	public boolean hasEnoughBalanceForSell(Wallet wallet, String pair, BigDecimal amount) {
        if (pair.equals("BTC/USDT")) {
            return wallet.getBtcAmount().compareTo(amount) >= 0;
        } else if (pair.equals("ETH/USDT")) {
            return wallet.getEthAmount().compareTo(amount) >= 0;
        }
		return false;
	}

	public void updateWallet(Wallet wallet, String pair, String type, BigDecimal amount, BigDecimal price) {
        if (type.equals("BUY")) {
            BigDecimal totalCost = amount.multiply(price);
            wallet.setUsdtBalance(wallet.getUsdtBalance().subtract(totalCost));
            if (pair.equals("BTC/USDT")) {
                wallet.setBtcAmount(wallet.getBtcAmount().add(amount));
            } else if (pair.equals("ETH/USDT")) {
                wallet.setEthAmount(wallet.getEthAmount().add(amount));
            }
        } else if (type.equals("SELL")) {
            if (pair.equals("BTC/USDT")) {
                wallet.setBtcAmount(wallet.getBtcAmount().subtract(amount));
            } else if (pair.equals("ETH/USDT")) {
                wallet.setEthAmount(wallet.getEthAmount().subtract(amount));
            }
            BigDecimal totalSaleAmount = amount.multiply(price);
            wallet.setUsdtBalance(wallet.getUsdtBalance().add(totalSaleAmount));
        }

        walletRepository.save(wallet);
	}

	
}
