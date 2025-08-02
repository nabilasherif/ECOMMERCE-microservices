package main.microservices.shop.service;

import jakarta.transaction.Transactional;
import main.microservices.shop.model.Product;
import main.microservices.shop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalStateException(
                        "Product with id " + productId + " does not exist"));
    }

    @Transactional
    public void updateProduct(Long productId, String name, BigDecimal price, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalStateException(
                        "Product with id " + productId + " does not exist"));

        if (name != null && !name.isEmpty() && !Objects.equals(product.getName(), name)) {
            product.setName(name);
        }

        if (price != null && price.compareTo(product.getPrice()) != 0) {
            product.setPrice(price);
        }

        if (quantity != null && !Objects.equals(product.getQuantity(), quantity)) {
            product.setQuantity(quantity);
        }
    }

    public void deleteProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new IllegalStateException(
                    "Product with id " + productId + " does not exist");
        }
        productRepository.deleteById(productId);
    }
}
