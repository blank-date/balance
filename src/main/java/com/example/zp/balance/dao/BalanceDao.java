package com.example.zp.balance.dao;

import com.example.zp.balance.model.Balance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BalanceDao extends JpaRepository<Balance, Long> {

    Optional<Balance> findByUserId(Long userId);
}
