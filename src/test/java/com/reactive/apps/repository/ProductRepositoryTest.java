package com.reactive.apps.repository;

import com.reactive.apps.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    /**
     * After each test execute ROLBACK automatically
     */
    @Test
    void save_ThenFindById_ReturnsSavedProduct() {
        // Given
        Product product = Product.builder()
                .qrCode("QR-001")
                .name("Laptop Dell XPS 15")
                .detail("Intel Core i7, 16GB RAM, 512GB SSD")
                .build();

        // When
        Product saved = productRepository.save(product);
        Optional<Product> found = productRepository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isNotNull();
        assertThat(found.get().getQrCode()).isEqualTo("QR-001");
        assertThat(found.get().getName()).isEqualTo("Laptop Dell XPS 15");
        assertThat(found.get().getDetail()).isEqualTo("Intel Core i7, 16GB RAM, 512GB SSD");
    }
    
    /**
     * Because of the automatically rollback, the DB is 
     * allways empty, so to delete sth firts I need to create it
     */
    @Test
    void save_ThenDelete_ThenFindById_ReturnsEmpty() {
    	//Given
    	Product product = Product.builder()
                .qrCode("QR-001")
                .name("Laptop Dell XPS 15")
                .detail("Intel Core i7, 16GB RAM, 512GB SSD")
                .build();
    	
    	// When - Then
    	Product saved = productRepository.save(product);
    	assertThat(productRepository.findById(saved.getId())).isPresent();
    	
    	productRepository.deleteById(saved.getId());
    	assertThat(productRepository.findById(saved.getId())).isEmpty();
        
    }
    
    @Test
    void existsById_ReturnsFalse() {
    	//Given
    	Long productId = 999L;
    	
    	// When
    	boolean exist = productRepository.existsById(productId);
    	
    	// Then
    	assertThat(exist).isFalse();
        
    }

}
