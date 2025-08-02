package main.microservices.wallet.service;

import jakarta.transaction.Transactional;
import main.microservices.wallet.model.Transaction;
import main.microservices.wallet.model.Wallet;
import main.microservices.wallet.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private  final WalletService walletService;

    @Autowired
    public TransactionService(final TransactionRepository transactionRepository, final WalletService walletService) {
        this.transactionRepository = transactionRepository;
        this.walletService = walletService;
    }

    @Transactional
    public void addTransaction(Long userId, BigDecimal amount) {
        Wallet wallet = walletService.getWalletByUserId(userId);

        walletService.updateWallet(wallet.getId(), amount);

        Transaction transaction = new Transaction(amount, LocalDateTime.now(), wallet);
        transactionRepository.save(transaction);
    }

    public List<Transaction> getTransactionsByUserId(Long userId) {
        Wallet wallet = walletService.getWalletByUserId(userId);
        return transactionRepository.findByWalletId(wallet.getId());
    }

}
