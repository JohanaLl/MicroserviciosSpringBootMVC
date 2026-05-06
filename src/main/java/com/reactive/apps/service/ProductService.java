package com.reactive.apps.service;

import com.reactive.apps.dto.ProductDTO;

import java.util.List;

public interface ProductService {

    List<ProductDTO> findAll();

    ProductDTO findById(Long id);

    ProductDTO save(ProductDTO dto);

    ProductDTO update(Long id, ProductDTO dto);

    void deleteById(Long id);

}
