package com.example.zp.balance.controller.dto;

import com.example.zp.balance.model.TransactionType;

import java.math.BigDecimal;

public record TransactionResult(
        Long userId,
        TransactionType transactionType,
        BigDecimal amount,
        BigDecimal endingBalance
) {
}
