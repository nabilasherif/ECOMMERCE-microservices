package main.microservices.wallet.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal  balance;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id") // foreign key
    private User user;

    public Wallet() {
        super();
    }

    public Wallet(User user) {
        super();
        this.balance = BigDecimal.valueOf(0);
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
