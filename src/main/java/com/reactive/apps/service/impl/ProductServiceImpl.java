package com.reactive.apps.service.impl;

import com.reactive.apps.exception.ProductNotFoundException;
import com.reactive.apps.model.Product;
import com.reactive.apps.repository.ProductRepository;
import com.reactive.apps.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> findById(Long id) {
        return Optional.of(productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id)));
    }

    @Override
    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Product update(Long id, Product product) {
        return productRepository.findById(id)
                .map(existing -> {
                    existing.setQrCode(product.getQrCode());
                    existing.setName(product.getName());
                    existing.setDetail(product.getDetail());
                    return productRepository.save(existing);
                })
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Override
    public void deleteById(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
    }

}
