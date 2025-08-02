package main.microservices.shop.controller;

import main.microservices.shop.model.Order;
import main.microservices.shop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static main.microservices.shop.security.JwtService.extractUserId;

@RestController
@RequestMapping(path = "api/shop/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            Order order = orderService.placeOrder(authorizationHeader);
            return ResponseEntity.ok(order);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getOrders(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            Long customerId = extractUserId(authorizationHeader);
            List<Order> orders = orderService.getOrders(customerId);
            return ResponseEntity.ok(orders);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
