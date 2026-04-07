package com.example.zp.balance.dao;

import com.example.zp.balance.model.BalanceTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BalanceTransactionDao extends JpaRepository<BalanceTransaction, Long> {
}
