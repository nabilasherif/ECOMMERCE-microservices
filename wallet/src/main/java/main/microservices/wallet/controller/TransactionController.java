package main.microservices.wallet.controller;

import main.microservices.wallet.model.Transaction;
import main.microservices.wallet.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static main.microservices.wallet.middleware.JwtService.extractUserId;

@RestController
@RequestMapping("api/wallet/transaction")
public class TransactionController {
    private final TransactionService transactionService;

    @Autowired
    public TransactionController(final TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<String> addTransaction(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody Map<String, BigDecimal> amount) {
        try {
            Long userId = extractUserId(authorizationHeader);
            BigDecimal transactionAmount = amount.get("amount");
            if (transactionAmount == null) {
                return ResponseEntity.badRequest().body("Amount must be provided");
            }

            transactionService.addTransaction(userId, transactionAmount);
            return ResponseEntity.ok("Transaction successful");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getTransactionsByUserId(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            Long userId = extractUserId(authorizationHeader);
            List<Transaction> transactions = transactionService.getTransactionsByUserId(userId);
            return ResponseEntity.ok(transactions);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
