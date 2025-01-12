package com.cyl.crypto_trading_system.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "WALLET_TBL")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private BigDecimal usdtBalance;
    private BigDecimal ethAmount;
    private BigDecimal btcAmount;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getUsdtBalance() {
		return usdtBalance;
	}

	public void setUsdtBalance(BigDecimal usdtBalance) {
		this.usdtBalance = usdtBalance;
	}

	public BigDecimal getEthAmount() {
		return ethAmount;
	}

	public void setEthAmount(BigDecimal ethAmount) {
		this.ethAmount = ethAmount;
	}

	public BigDecimal getBtcAmount() {
		return btcAmount;
	}

	public void setBtcAmount(BigDecimal btcAmount) {
		this.btcAmount = btcAmount;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}