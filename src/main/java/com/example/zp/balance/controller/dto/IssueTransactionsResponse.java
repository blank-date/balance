package com.example.zp.balance.controller.dto;

import java.util.List;

public record IssueTransactionsResponse(List<TransactionResult> results) {
}
