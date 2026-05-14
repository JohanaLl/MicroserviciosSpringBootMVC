package com.reactive.apps.service;

import com.reactive.apps.dto.ProductDTO;
import com.reactive.apps.exception.ProductNotFoundException;
import com.reactive.apps.metrics.ProductMetricsCollector;
import com.reactive.apps.model.Product;
import com.reactive.apps.repository.ProductRepository;
import com.reactive.apps.service.impl.ProductServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

	/**
	 * Crea un doble falso de ProductRepository
	 * No es el repositorio real — es un objeto que tiene los mismos métodos 
	 * pero no hace nada por defecto.
	 * Se mockea todo lo que no esta dentro de la clase que quiero probar
	 */
    @Mock
    private ProductRepository productRepository;

    /**
     * Crea una instancia real de ProductServiceImpl 
     * e inyecta automáticamente el @Mock que declaraste arriba.
     */
    @InjectMocks
    private ProductServiceImpl productService;

    /**
     * JUnit ejecuta este método antes de cada prueba.
     */
    @BeforeEach
    void setUp() {
        ProductMetricsCollector.getInstance().reset();
    }

    @Test
    void findById_WhenProductExists_ReturnsProductDTO() {
        // Given
        Long productId = 1L;
        Product product = Product.builder()
                .id(productId)
                .qrCode("QR-001")
                .name("Laptop Dell XPS 15")
                .detail("Intel Core i7, 16GB RAM, 512GB SSD")
                .build();

        /**
         * Configura el comportamiento del mock
         */
        //Mock de prueba
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // When
        ProductDTO result = productService.findById(productId);

        /**
         * No es de Mockito sino de AssertJ, pero trabaja junto. 
         * Verifica el valor de retorno — que el objeto tiene exactamente los datos que esperabas. 
         * Mockito verifica cómo se llamaron los mocks, AssertJ verifica qué devolvió el método.
         */
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(productId);
        assertThat(result.getQrCode()).isEqualTo("QR-001");
        assertThat(result.getName()).isEqualTo("Laptop Dell XPS 15");
        assertThat(result.getDetail()).isEqualTo("Intel Core i7, 16GB RAM, 512GB SSD");

        /**
         * Verifica que el mock fue llamado de la manera esperada.
         * times(1) — se llamó exactamente una vez. Si se llamó dos veces o cero, la prueba falla.
	     * .findById(productId) — se llamó con ese argumento específico.
         */
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void findById_WhenProductDoesNotExist_ThrowsProductNotFoundException() {
        // Given
        Long productId = 99L;
        //Mock de prueba
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> productService.findById(productId))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("99");

        verify(productRepository, times(1)).findById(productId);
    }
    
    @Test
    void findAll_WhenProductsExists_ReturnsProductListDTO() {
    	// Given        
        List<Product> products = List.of(
        		Product.builder().id(1L).qrCode("QR-001").name("Laptop Dell XPS 15").detail("Intel Core i7").build(),
        		Product.builder().id(2L).qrCode("QR-002").name("Mouse Logitech").detail("Inalámbrico").build(),
        	    Product.builder().id(3L).qrCode("QR-003").name("Teclado HP").detail("USB mecánico").build()
        );
        
        //Mock de prueba
        when(productRepository.findAll()).thenReturn(products);
        		
        //When
        List<ProductDTO> result = productService.findAll();
        
        /**
         * Como esperas que los datos retornen
         * Todos los assertThat deberian retornar true
         */
        //Then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getName()).isEqualTo("Laptop Dell XPS 15");
        assertThat(result.get(1).getId()).isEqualTo(2L);
        assertThat(result.get(2).getId()).isEqualTo(3L);
        
        verify(productRepository, times(1)).findAll();
    }
    
    @Test
    void findAll_WhenProductsDoesNotExists_ReturnsEmptyListDTO() {
    	// Given        
        List<Product> products = List.of();
        
        //Mock de prueba
        when(productRepository.findAll()).thenReturn(products);
        		
        //When
        List<ProductDTO> result = productService.findAll();
        
        //Then
        assertThat(result).hasSize(0);
        
        verify(productRepository, times(1)).findAll();
    }
    
    @Test
    void save_WithValidDTO_ReturnsProductDTO() {
        // Given
        ProductDTO productDto = ProductDTO.builder()
                .qrCode("QR-001")
                .name("Laptop Dell XPS 15")
                .detail("Intel Core i7, 16GB RAM, 512GB SSD")
                .build();
        
        Product savedProduct = Product.builder()
        		.id(1L)
                .qrCode("QR-001")
                .name("Laptop Dell XPS 15")
                .detail("Intel Core i7, 16GB RAM, 512GB SSD")
                .build();

        /**
         * any(Product.class) — le dice a Mockito: "no me importa qué instancia exacta, 
         * si llaman save con cualquier Product, responde con esto".
         */
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // When
        ProductDTO result = productService.save(productDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getQrCode()).isEqualTo("QR-001");
        assertThat(result.getName()).isEqualTo("Laptop Dell XPS 15");
        assertThat(result.getDetail()).isEqualTo("Intel Core i7, 16GB RAM, 512GB SSD");

        verify(productRepository, times(1)).save(any(Product.class));
    }
    
    @Test
    void update_WhenProductExist_ReturnsUpdateProducDTO() {
    	//Given
    	Long productId = 1L;
    	ProductDTO productDto = ProductDTO.builder()
                .qrCode("QR-002")
                .name("Laptop Dell")
                .detail("Intel Core i7, 16GB RAM, 2T SSD")
                .build();
    	
    	// producto que ya existe en BD — datos originales
    	Product existingProduct = Product.builder()
    	        .id(1L)
    	        .qrCode("QR-001")           // ← datos viejos
    	        .name("Laptop Dell XPS 15")
    	        .detail("Intel Core i7, 16GB RAM, 512GB SSD")
    	        .build();

    	// lo que devuelve el repositorio después de guardar — datos nuevos
    	Product updatedProduct = Product.builder()
    	        .id(1L)
    	        .qrCode("QR-002")           // ← datos nuevos
    	        .name("Laptop Dell")
    	        .detail("Intel Core i7, 16GB RAM, 2T SSD")
    	        .build();
    	
    	when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
    	when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);
    	
    	// When
    	ProductDTO result = productService.update(productId, productDto);
    	
    	// Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getQrCode()).isEqualTo("QR-002");
        assertThat(result.getName()).isEqualTo("Laptop Dell");
        assertThat(result.getDetail()).isEqualTo("Intel Core i7, 16GB RAM, 2T SSD");

        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(any(Product.class));
    }
    
    @Test
    void update_WhenProductDoesNotExist_ThrowsProductNotFoundException() {
    	//Given
    	Long productId = 99L;
    	ProductDTO productDto = ProductDTO.builder()
                .qrCode("QR-002")
                .name("Laptop Dell")
                .detail("Intel Core i7, 16GB RAM, 2T SSD")
                .build();

    	/**
         * Como se debe comportar el mock
         */
    	//Mock de prueba
    	when(productRepository.findById(productId)).thenReturn(Optional.empty());;
    	
    	// When - Then
    	assertThatThrownBy(() -> productService.update(productId, productDto))
					        .isInstanceOf(ProductNotFoundException.class)
					        .hasMessageContaining("99");
    	
    	verify(productRepository, times(1)).findById(productId);
    }
    
    /*
     * deleteById  — void. 
     * NO se puede usar assertThat porque no hay resultado.
     */
    @Test
    void delete_WhenProductExist_DeteleProduct() {
    	//Given
    	Long productId = 1L;
    	
    	//Mock de prueba
    	when(productRepository.existsById(productId)).thenReturn(true);
    	
    	//When - Then
    	productService.deleteById(productId);
    	
    	verify(productRepository, times(1)).existsById(productId);
        verify(productRepository, times(1)).deleteById(productId);
    }
    
    @Test
    void delete_WhenProductDoesNotExist_ThrowsProductNotFoundException() {
    	//Given
    	Long productId = 99L;
    	
    	//Mock de prueba
    	when(productRepository.existsById(productId)).thenReturn(false);
    	
    	// When - Then
    	assertThatThrownBy(() -> productService.deleteById(productId))
					        .isInstanceOf(ProductNotFoundException.class)
					        .hasMessageContaining("99");
    	   	
    	verify(productRepository, times(1)).existsById(productId);
    }

}
