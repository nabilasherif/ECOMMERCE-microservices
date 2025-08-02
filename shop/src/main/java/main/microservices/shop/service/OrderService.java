package main.microservices.shop.service;

import jakarta.transaction.Transactional;
import main.microservices.shop.proxy.WalletProxy;
import main.microservices.shop.model.Cart;
import main.microservices.shop.model.CartItem;
import main.microservices.shop.model.Order;
import main.microservices.shop.model.OrderItem;
import main.microservices.shop.repository.OrderRepository;
import main.microservices.shop.model.Product;
import main.microservices.shop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static main.microservices.shop.security.JwtService.extractUserId;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;
    @Autowired
    private final WalletProxy walletProxy;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        CartService cartService,
                        WalletProxy walletProxy) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
        this.walletProxy = walletProxy;
    }

    @Transactional
    public Order placeOrder(String authorizationHeader){
        Long customerId = extractUserId(authorizationHeader);
        Cart cart = cartService.getCart(customerId);
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty.");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cart.getItems()) {
            Product product = productRepository.findById(cartItem.getProduct().getId())
                    .orElseThrow(() -> new IllegalStateException(
                            "Product not found: " + cartItem.getProduct().getId()));

            if (cartItem.getQuantity() > product.getQuantity()) {
                throw new IllegalStateException("Not enough stock for product: " + product.getName());
            }

            totalAmount = totalAmount.add(product.getPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity())));

            orderItems.add(new OrderItem(product.getId(), product.getName(),
                    product.getPrice(), cartItem.getQuantity()));
        }

        BigDecimal balance = walletProxy.getBalance(authorizationHeader).getBody();
        if (balance.compareTo(totalAmount) < 0) {
            throw new IllegalStateException("Insufficient wallet balance.");
        }

        for (CartItem cartItem : cart.getItems()) {
            Product product = productRepository.findById(cartItem.getProduct().getId()).get();
            product.setQuantity(product.getQuantity() - cartItem.getQuantity());
            productRepository.save(product);
        }

        Map<String, BigDecimal> request = new HashMap<>();
        request.put("amount", totalAmount.negate());
        walletProxy.updateBalance(authorizationHeader, request);
        walletProxy.addTransaction(authorizationHeader, request);

        Order order = new Order(customerId);
        order.setItems(orderItems);
        Order savedOrder = orderRepository.save(order);

        cartService.clearCart(customerId);

        return savedOrder;
    }

    public List<Order> getOrders(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }
}
