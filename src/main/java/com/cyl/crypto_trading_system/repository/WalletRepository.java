package com.cyl.crypto_trading_system.repository;

import com.cyl.crypto_trading_system.model.User;
import com.cyl.crypto_trading_system.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

	 Wallet findByUser(User user);
	 
}
