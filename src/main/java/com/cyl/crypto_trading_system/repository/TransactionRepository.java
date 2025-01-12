package com.cyl.crypto_trading_system.repository;

import com.cyl.crypto_trading_system.model.Transaction;
import com.cyl.crypto_trading_system.model.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	 List<Transaction> findByUser(User user);
	 
}
