package com.example.zp.balance.controller.dto;

import java.math.BigDecimal;

public record BalanceResponse(Long userId, BigDecimal balance) {
}
