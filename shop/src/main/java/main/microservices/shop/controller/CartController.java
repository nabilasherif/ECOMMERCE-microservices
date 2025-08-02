package main.microservices.shop.controller;

import main.microservices.shop.service.CartService;
import main.microservices.shop.model.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static main.microservices.shop.security.JwtService.extractUserId;

@RestController
@RequestMapping(path = "api/shop/cart")
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping
    public ResponseEntity<?> createCart(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            Long customerId = extractUserId(authorizationHeader);
            Cart cart = cartService.createCart(customerId);
            return ResponseEntity.ok(cart);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getCart(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            Long customerId = extractUserId(authorizationHeader);
            return ResponseEntity.ok(cartService.getCart(customerId));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addProductToCart(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody Map<String, Object> requestBody) {
        try {
            Long customerId = extractUserId(authorizationHeader);

            Long productId = Long.valueOf(requestBody.get("productId").toString());
            int quantity = Integer.parseInt(requestBody.get("quantity").toString());

            Cart updatedCart = cartService.addProductToCart(customerId, productId, quantity);
            return ResponseEntity.ok(updatedCart);
        } catch (IllegalStateException | NullPointerException | NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid request: " + e.getMessage());
        }
    }


    @DeleteMapping("/remove")
    public ResponseEntity<?> removeProductFromCart(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam Long productId) {
        try {
            Long customerId = extractUserId(authorizationHeader);
            Cart updatedCart = cartService.removeProductFromCart(customerId, productId);
            return ResponseEntity.ok(updatedCart);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //may be deleted later, use the service only whenever placing order
    @DeleteMapping("/{customerId}/clear")
    public ResponseEntity<String> clearCart(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            Long customerId = extractUserId(authorizationHeader);
            cartService.clearCart(customerId);
            return ResponseEntity.ok("Cart cleared for customer ID: " + customerId);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
