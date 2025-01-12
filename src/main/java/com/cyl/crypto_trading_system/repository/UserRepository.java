package com.cyl.crypto_trading_system.repository;

import com.cyl.crypto_trading_system.model.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

	boolean existsByUsername(String username);
	Optional<User> findById(Long id);

}
