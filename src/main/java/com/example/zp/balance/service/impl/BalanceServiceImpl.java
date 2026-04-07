package com.example.zp.balance.service.impl;

import com.example.zp.balance.controller.dto.BalanceResponse;
import com.example.zp.balance.controller.dto.IssueTransactionsRequest;
import com.example.zp.balance.controller.dto.IssueTransactionsResponse;
import com.example.zp.balance.controller.dto.TransactionRequest;
import com.example.zp.balance.controller.dto.TransactionResult;
import com.example.zp.balance.dao.BalanceDao;
import com.example.zp.balance.dao.BalanceTransactionDao;
import com.example.zp.balance.exception.BusinessException;
import com.example.zp.balance.model.Balance;
import com.example.zp.balance.model.BalanceTransaction;
import com.example.zp.balance.model.TransactionType;
import com.example.zp.balance.service.BalanceService;
import jakarta.persistence.OptimisticLockException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class BalanceServiceImpl implements BalanceService {

    private static final int MAX_RETRIES = 3;

    private final BalanceDao balanceDao;
    private final BalanceTransactionDao balanceTransactionDao;
    private final boolean checkBalance;
    private final TransactionTemplate transactionTemplate;

    public BalanceServiceImpl(
            BalanceDao balanceDao,
            BalanceTransactionDao balanceTransactionDao,
            PlatformTransactionManager transactionManager,
            @Value("${checkBalance:true}") boolean checkBalance
    ) {
        this.balanceDao = balanceDao;
        this.balanceTransactionDao = balanceTransactionDao;
        this.checkBalance = checkBalance;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    @Override
    public IssueTransactionsResponse issueTransactions(IssueTransactionsRequest request) {
        // 乐观锁+重试 保证并发安全
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                return transactionTemplate.execute(status -> {
                    try {
                        return doIssueTransactions(request);
                        // 业务异常（如余额不能为负，保证单条数据回滚，事物隔离）
                    } catch (RuntimeException ex) {
                        status.setRollbackOnly();
                        throw ex;
                    }
                });
            } catch (OptimisticLockException | OptimisticLockingFailureException ex) {
                if (attempt == MAX_RETRIES) {
                    throw ex;
                }
            }
        }
        throw new IllegalStateException("Unexpected transaction state");
    }

    @Override
    @Transactional(readOnly = true)
    public BalanceResponse getBalance(Long userId) {
        Balance balance = balanceDao.findByUserId(userId)
                .orElseGet(() -> new Balance(userId, BigDecimal.ZERO));
        return new BalanceResponse(userId, balance.getBalance());
    }

    /**
     * 处理交易并发操作
     *
     * @param request
     * @return
     */
    private IssueTransactionsResponse doIssueTransactions(IssueTransactionsRequest request) {
        // 排序请求，相当于给用户的交易分组处理，减少对用户余额的锁持有时间，提高线程效率
        List<TransactionRequest> sortedTransactions = request.transactions().stream()
                .sorted(Comparator.comparing(TransactionRequest::userId))
                .toList();

        List<TransactionResult> results = new ArrayList<>();
        for (TransactionRequest transaction : sortedTransactions) {
            results.add(this.applyTransaction(transaction));
        }
        return new IssueTransactionsResponse(results);
    }

    /**
     * 处理单笔交易
     *
     * @param request
     * @return
     */
    private TransactionResult applyTransaction(TransactionRequest request) {
        // 校验金额
        this.validateAmount(request.amount());

        // 未查询到主动创建一个
        Balance balance = balanceDao.findByUserId(request.userId())
                .orElseGet(() -> new Balance(request.userId(), BigDecimal.ZERO));

        BigDecimal updatedBalance = balance.getBalance().add(request.amount());
        // 配置项，如果是true，不允许用户余额为负
        if (checkBalance && updatedBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Insufficient balance for userId=" + request.userId());
        }

        balance.setBalance(updatedBalance);
        // 注解声明乐观锁+1版本号更新
        Balance savedBalance = balanceDao.saveAndFlush(balance);

        // 保存收支明细
        TransactionType transactionType = this.resolveType(request.amount());
        BalanceTransaction transaction = new BalanceTransaction(
                request.userId(),
                transactionType,
                request.amount(),
                savedBalance.getBalance()
        );
        balanceTransactionDao.save(transaction);

        return new TransactionResult(
                request.userId(),
                transactionType,
                request.amount(),
                savedBalance.getBalance()
        );
    }

    /**
     * 校验请求金额
     * 0 无意义
     * <0 支出
     * >0 收入
     *
     * @param amount
     */
    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
            throw new BusinessException("Transaction amount must not be zero");
        }
    }

    /**
     * 判断收支状态
     *
     * @param amount
     * @return
     */
    private TransactionType resolveType(BigDecimal amount) {
        return amount.compareTo(BigDecimal.ZERO) > 0 ? TransactionType.INCOME : TransactionType.EXPENSE;
    }
}
