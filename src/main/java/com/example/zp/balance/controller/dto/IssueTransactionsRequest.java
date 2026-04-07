package com.example.zp.balance.controller.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record IssueTransactionsRequest(
        @NotEmpty(message = "transactions must not be empty")
        List<@Valid TransactionRequest> transactions
) {
}
