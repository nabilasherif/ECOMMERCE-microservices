package main.microservices.shop.service;

import jakarta.transaction.Transactional;
import main.microservices.shop.model.Cart;
import main.microservices.shop.model.CartItem;
import main.microservices.shop.model.Product;
import main.microservices.shop.repository.ProductRepository;
import main.microservices.shop.repository.CartRepository;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    public Cart createCart(Long customerId) {
        return cartRepository.findByCustomerId(customerId)
                .orElseGet(() -> cartRepository.save(new Cart(customerId, null)));
    }

    public Cart getCart(Long customerId) {
        return cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new IllegalStateException("Cart for customer " + customerId + " does not exist"));
    }

    @Transactional
    public Cart addProductToCart(Long customerId, Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalStateException("Quantity must be greater than 0");
        }

        Cart cart = getCart(customerId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalStateException("Product not found with ID: " + productId));

        if (quantity > product.getQuantity()) {
            throw new IllegalStateException("Not enough stock for product: " + product.getName());
        }

        boolean updated = false;
        for (CartItem cartItem : cart.getItems()) {
            if (cartItem.getProduct().getId().equals(productId)) {
                int newQuantity = cartItem.getQuantity() + quantity;
                if (newQuantity > product.getQuantity()) {
                    throw new IllegalStateException("Cannot add more than available stock");
                }
                cartItem.setQuantity(newQuantity);
                updated = true;
                break;
            }
        }

        if (!updated) {
            cart.getItems().add(new CartItem(product, quantity));
        }

        return cartRepository.save(cart);
    }

    public Cart removeProductFromCart(Long customerId, Long productId) {
        Cart cart = getCart(customerId);
        cart.getItems().removeIf(cartItem -> cartItem.getProduct().getId().equals(productId));
        return cartRepository.save(cart);
    }

    public void clearCart(Long customerId) {
        Cart cart = getCart(customerId);
        cart.getItems().clear();
        cartRepository.save(cart);
    }
}
