package com.example.zp.balance.controller.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransactionRequest(
        @NotNull(message = "userId must not be null")
        Long userId,
        @NotNull(message = "amount must not be null")
        BigDecimal amount
) {
}
