package main.microservices.wallet.controller;

import main.microservices.wallet.model.User;
import main.microservices.wallet.repository.UserRepository;
import main.microservices.wallet.model.Wallet;
import main.microservices.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

import static main.microservices.wallet.middleware.JwtService.extractUserId;

@RestController
@RequestMapping(path = "api/wallet")
public class WalletController {
    private final WalletService walletService;
    private final UserRepository userRepository;

    @Autowired
    public WalletController(final WalletService walletService, UserRepository userRepository) {
        this.walletService = walletService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<String> createWallet(@RequestHeader("Authorization") String authorizationHeader) {
        Long userId = extractUserId(authorizationHeader);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        try {
            walletService.createWallet(new Wallet(user));
            return ResponseEntity.ok("Wallet created for user with ID: " + user.getId());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/balance")
    public ResponseEntity<?> getBalance(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            Long userId = extractUserId(authorizationHeader);
            Wallet wallet = walletService.getWalletByUserId(userId);
            return ResponseEntity.ok(wallet.getBalance());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping(path = "{walletId}")
    public ResponseEntity<String> deleteWallet(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            Long userId = extractUserId(authorizationHeader);
            walletService.deleteWalletByUserId(userId);
            return ResponseEntity.ok("Wallet deleted successfully for user with ID: " + userId);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/balance")
    public ResponseEntity<String> updateBalance(@RequestHeader("Authorization") String authorizationHeader,
                                                @RequestBody Map<String, BigDecimal> request) {
        try {
            Long userId = extractUserId(authorizationHeader);
            BigDecimal amount = request.get("amount");
            walletService.updateWallet(
                    walletService.getWalletByUserId(userId).getId(),
                    amount);
            return ResponseEntity.ok("Balance updated successfully for user with Id "+ userId);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
