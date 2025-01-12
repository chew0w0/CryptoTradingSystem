package com.cyl.crypto_trading_system.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

@Service
public class PriceAggregatorService {

    private static final String BINANCE_URL = "https://api.binance.com/api/v3/ticker/bookTicker";
    private static final String HUOBI_URL = "https://api.huobi.pro/market/tickers";
    
    private BigDecimal btcBestBidPrice;
    private BigDecimal ethBestBidPrice;
    private BigDecimal btcBestAskPrice;
    private BigDecimal ethBestAskPrice;

    @Scheduled(fixedRate = 10000)
    public void fetchPrices() {
        RestTemplate restTemplate = new RestTemplate();
        
        String binanceResponse = restTemplate.getForObject(BINANCE_URL, String.class);
        String huobiResponse = restTemplate.getForObject(HUOBI_URL, String.class);
        
        compareAndUpdatePrices(binanceResponse, huobiResponse);
    }

    private void compareAndUpdatePrices(String binanceResponse, String huobiResponse) {
        BigDecimal btcBinanceBidPrice = extractPriceFromBinance(binanceResponse, "bidPrice", "BTCUSDT");
        BigDecimal btcBinanceAskPrice = extractPriceFromBinance(binanceResponse, "askPrice", "BTCUSDT");
        BigDecimal ethBinanceBidPrice = extractPriceFromBinance(binanceResponse, "bidPrice", "ETHUSDT");
        BigDecimal ethBinanceAskPrice = extractPriceFromBinance(binanceResponse, "askPrice", "ETHUSDT");
        
        BigDecimal btcHuobiBidPrice = extractPriceFromHuobi(huobiResponse, "bid", "btcusdt");
        BigDecimal btcHuobiAskPrice = extractPriceFromHuobi(huobiResponse, "ask", "btcusdt");
        BigDecimal ethHuobiBidPrice = extractPriceFromHuobi(huobiResponse, "bid", "ethusdt");
        BigDecimal ethHuobiAskPrice = extractPriceFromHuobi(huobiResponse, "ask", "ethusdt");
        
        this.btcBestBidPrice = btcBinanceBidPrice.compareTo(btcHuobiBidPrice) > 0 ? btcBinanceBidPrice : btcHuobiBidPrice;
        this.btcBestAskPrice = btcBinanceAskPrice.compareTo(btcHuobiAskPrice) < 0 ? btcBinanceAskPrice : btcHuobiAskPrice;
        this.ethBestBidPrice = ethBinanceBidPrice.compareTo(ethHuobiBidPrice) > 0 ? ethBinanceBidPrice : ethHuobiBidPrice;
        this.ethBestAskPrice = ethBinanceAskPrice.compareTo(ethHuobiAskPrice) < 0 ? ethBinanceAskPrice : ethHuobiAskPrice;
    }

    private BigDecimal extractPriceFromBinance(String response, String priceType, String symbol) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);
            for (JsonNode tickerNode : rootNode) {
                if (tickerNode.path("symbol").asText().equalsIgnoreCase(symbol)) {
                    String price = tickerNode.path(priceType).asText();
                    return new BigDecimal(price);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal extractPriceFromHuobi(String response, String priceType, String symbol) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode tickersNode = rootNode.path("data");
            for (JsonNode tickerNode : tickersNode) {
                String tickerSymbol = tickerNode.path("symbol").asText();
                if (tickerSymbol.equalsIgnoreCase(symbol)) {
                    String price = tickerNode.path(priceType).asText();
                    return new BigDecimal(price);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO; 
    }
    
	public BigDecimal getBtcBestBidPrice() {
		return btcBestBidPrice != null ? btcBestBidPrice : BigDecimal.ZERO;
	}

	public void setBtcBestBidPrice(BigDecimal btcBestBidPrice) {
		this.btcBestBidPrice = btcBestBidPrice;
	}

	public BigDecimal getEthBestBidPrice() {
		return ethBestBidPrice != null ? ethBestBidPrice : BigDecimal.ZERO;
	}

	public void setEthBestBidPrice(BigDecimal ethBestBidPrice) {
		this.ethBestBidPrice = ethBestBidPrice;
	}

	public BigDecimal getBtcBestAskPrice() {
		return btcBestAskPrice != null ? btcBestAskPrice : BigDecimal.ZERO;
	}

	public void setBtcBestAskPrice(BigDecimal btcBestAskPrice) {
		this.btcBestAskPrice = btcBestAskPrice;
	}

	public BigDecimal getEthBestAskPrice() {
		return ethBestAskPrice != null ? ethBestAskPrice : BigDecimal.ZERO;
	}

	public void setEthBestAskPrice(BigDecimal ethBestAskPrice) {
		this.ethBestAskPrice = ethBestAskPrice;
	}

    public BigDecimal getPriceForPair(String pair, String type) {
        if (pair.equals("BTC/USDT")) {
        	if (type.equals("BUY")) {
        		return getBtcBestAskPrice();
        	} else if (type.equals("SELL")) {
                return getBtcBestBidPrice();
        	}
        } else if (pair.equals("ETH/USDT")) {
        	if (type.equals("BUY")) {
        		return getEthBestAskPrice();
        	} else if (type.equals("SELL")) {
                return getEthBestBidPrice();
        	}
        }
        return BigDecimal.ZERO;
    }
    
}
