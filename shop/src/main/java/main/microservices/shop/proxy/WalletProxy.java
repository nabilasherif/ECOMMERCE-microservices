package main.microservices.shop.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@FeignClient(name = "wallet-service", url = "http://localhost:8070/api/wallet")
public interface WalletProxy {

    // Wallet endpoints

    @GetMapping("/balance")
    ResponseEntity<BigDecimal> getBalance(@RequestHeader("Authorization") String authorizationHeader);

    @PutMapping("/balance")
    ResponseEntity<String> updateBalance(@RequestHeader("Authorization") String authorizationHeader,
                                         @RequestBody Map<String, BigDecimal> request);

    @PostMapping
    ResponseEntity<String> createWallet(@RequestHeader("Authorization") String authorizationHeader);

    // Transaction endpoints

    @PostMapping("/transaction")
    ResponseEntity<String> addTransaction(@RequestHeader("Authorization") String authorizationHeader,
                                          @RequestBody Map<String, BigDecimal> amount);

    @GetMapping("/transaction")
    ResponseEntity<List<Object>> getTransactionsByUserId(@RequestHeader("Authorization") String authorizationHeader);
}
