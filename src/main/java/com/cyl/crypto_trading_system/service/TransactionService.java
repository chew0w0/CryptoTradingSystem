package com.cyl.crypto_trading_system.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cyl.crypto_trading_system.model.Transaction;
import com.cyl.crypto_trading_system.model.User;
import com.cyl.crypto_trading_system.repository.TransactionRepository;

@Service
public class TransactionService {

	private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

	public List<Transaction> getUserTransactions(User user) {
		return transactionRepository.findByUser(user);
	}

	public void executeTrade(String pair, String type, BigDecimal amount, BigDecimal price, User user) {
		Transaction transaction = new Transaction();
        transaction.setCrypto_pair(pair);
        transaction.setOrder_type(type);
        transaction.setAmount(amount);
        transaction.setPrice(price);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setUser(user);
        transactionRepository.save(transaction);
	}

}
