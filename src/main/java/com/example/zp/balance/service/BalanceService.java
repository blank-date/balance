package com.example.zp.balance.service;

import com.example.zp.balance.controller.dto.BalanceResponse;
import com.example.zp.balance.controller.dto.IssueTransactionsRequest;
import com.example.zp.balance.controller.dto.IssueTransactionsResponse;

public interface BalanceService {

    IssueTransactionsResponse issueTransactions(IssueTransactionsRequest request);

    BalanceResponse getBalance(Long userId);
}
