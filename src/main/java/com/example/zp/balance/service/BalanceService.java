package com.example.zp.balance.service;

import com.example.zp.balance.controller.dto.BalanceResponse;
import com.example.zp.balance.controller.dto.IssueTransactionsRequest;
import com.example.zp.balance.controller.dto.IssueTransactionsResponse;

public interface BalanceService {

    /**
     * 处理交易
     *
     * @param request
     * @return
     */
    IssueTransactionsResponse issueTransactions(IssueTransactionsRequest request);

    /**
     * 获取用户余额
     *
     * @param userId
     * @return
     */
    BalanceResponse getBalance(Long userId);
}
