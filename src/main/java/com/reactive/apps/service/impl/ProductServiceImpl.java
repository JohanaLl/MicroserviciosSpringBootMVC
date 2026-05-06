package com.reactive.apps.service.impl;

import com.reactive.apps.dto.ProductDTO;
import com.reactive.apps.exception.ProductNotFoundException;
import com.reactive.apps.metrics.ProductMetricsCollector;
import com.reactive.apps.model.Product;
import com.reactive.apps.repository.ProductRepository;
import com.reactive.apps.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public List<ProductDTO> findAll() {
        List<ProductDTO> products = productRepository.findAll()
                .stream()
                .map(ProductDTO::fromEntity)
                .toList();
        ProductMetricsCollector.getInstance().recordFetch();
        return products;
    }

    @Override
    public ProductDTO findById(Long id) {
        ProductDTO dto = ProductDTO.fromEntity(
                productRepository.findById(id)
                        .orElseThrow(() -> new ProductNotFoundException(id))
        );
        ProductMetricsCollector.getInstance().recordFetch();
        return dto;
    }

    @Override
    public ProductDTO save(ProductDTO dto) {
        Product saved = productRepository.save(dto.toEntity());
        ProductMetricsCollector.getInstance().recordCreate();
        return ProductDTO.fromEntity(saved);
    }

    @Override
    public ProductDTO update(Long id, ProductDTO dto) {
        Product updated = productRepository.findById(id)
                .map(existing -> {
                    existing.setQrCode(dto.getQrCode());
                    existing.setName(dto.getName());
                    existing.setDetail(dto.getDetail());
                    return productRepository.save(existing);
                })
                .orElseThrow(() -> new ProductNotFoundException(id));
        ProductMetricsCollector.getInstance().recordUpdate();
        return ProductDTO.fromEntity(updated);
    }

    @Override
    public void deleteById(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
        ProductMetricsCollector.getInstance().recordDelete();
    }

}
