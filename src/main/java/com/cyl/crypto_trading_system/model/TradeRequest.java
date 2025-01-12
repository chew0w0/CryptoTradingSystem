package com.cyl.crypto_trading_system.model;

import java.math.BigDecimal;

public class TradeRequest {

    private String pair;
    private String type; 
    private BigDecimal amount;
    
	public String getPair() {
		return pair;
	}
	public void setPair(String pair) {
		this.pair = pair;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
}
