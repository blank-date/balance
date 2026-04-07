package com.example.zp.balance.controller;

import com.example.zp.balance.controller.dto.BalanceResponse;
import com.example.zp.balance.controller.dto.IssueTransactionsRequest;
import com.example.zp.balance.controller.dto.IssueTransactionsResponse;
import com.example.zp.balance.service.BalanceService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/balances")
public class BalanceController {

    private final BalanceService balanceService;

    public BalanceController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    /** 发起交易 */
    @PostMapping("/transactions")
    public IssueTransactionsResponse issueTransactions(@Valid @RequestBody IssueTransactionsRequest request) {
        return balanceService.issueTransactions(request);
    }

    /** 查询用户余额 */
    @GetMapping("/{userId}")
    public BalanceResponse getBalance(@PathVariable Long userId) {
        return balanceService.getBalance(userId);
    }
}
