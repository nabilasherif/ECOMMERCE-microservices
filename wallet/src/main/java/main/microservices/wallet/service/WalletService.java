package main.microservices.wallet.service;

import jakarta.transaction.Transactional;
import main.microservices.wallet.model.Wallet;
import main.microservices.wallet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class WalletService {

    private final WalletRepository walletRepository;

    @Autowired
    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public void createWallet(Wallet wallet) {
        Optional<Wallet> walletOptional = walletRepository.findWalletByUserId(wallet.getUser().getId());
        if (walletOptional.isPresent()) {
            throw new IllegalStateException("Wallet already exists");
        }
        walletRepository.save(wallet);
    }

    public Wallet getWalletByUserId(long userId) {
        return walletRepository.findWalletByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Wallet not found"));
    }

    @Transactional
    public void updateWallet(Long walletId, BigDecimal amount) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new IllegalStateException("Wallet does not exist"));

        BigDecimal newBalance = wallet.getBalance().add(amount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Not enough balance");
        }
        wallet.setBalance(newBalance);

        walletRepository.save(wallet);
    }


    public void deleteWalletByUserId(Long userId) {
        boolean walletExists = walletRepository.existsByUserId(userId);
        if (!walletExists) {
            throw new IllegalStateException("Wallet does not exist");
        }
        walletRepository.deleteByUserId(userId);
    }

}
