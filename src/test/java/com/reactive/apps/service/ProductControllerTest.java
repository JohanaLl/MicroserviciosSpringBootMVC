package com.reactive.apps.service;

import com.reactive.apps.controller.ProductController;
import com.reactive.apps.dto.ProductDTO;
import com.reactive.apps.exception.ProductNotFoundException;
import com.reactive.apps.metrics.ProductMetricsCollector;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @BeforeEach
    void setUp() {
        ProductMetricsCollector.getInstance().reset();
    }

    @Test
    void findAll_WhenProductsExists_Returns200WithProductListDTO() throws Exception {
        // Given
    	List<ProductDTO> products = List.of(
        		ProductDTO.builder().id(1L).qrCode("QR-001").name("Laptop Dell XPS 15").detail("Intel Core i7").build(),
        		ProductDTO.builder().id(2L).qrCode("QR-002").name("Mouse Logitech").detail("Inalámbrico").build(),
        		ProductDTO.builder().id(3L).qrCode("QR-003").name("Teclado HP").detail("USB mecánico").build()
        );
    	
    	when(productService.findAll()).thenReturn(products);
    	
        // When  — mockMvc.perform(get("/api/products"))
        // Then  — .andExpect(status().isOk())
    	mockMvc.perform(get("/api/products"))
    			.andExpect(status().isOk())
    			.andExpect(jsonPath("$.length()").value(3))
    			.andExpect(jsonPath("$[0].name").value("Laptop Dell XPS 15"))
    			.andExpect(jsonPath("$[1].id").value(2));
    			
    }
    
    @Test
    void findAll_WhenProductsDoesNotExists_Returns200WithEmptyListDTO() throws Exception {
        // Given
    	List<ProductDTO> products = List.of();
    	
    	when(productService.findAll()).thenReturn(products);
    	
        // When / Then 
    	mockMvc.perform(get("/api/products"))
    			.andExpect(status().isOk())
    			.andExpect(jsonPath("$.length()").value(0));
    			
    }

    @Test
    void findById_WhenProductExists_Returns200WithProductDTO() throws Exception {
        // Given
        Long productId = 1L;
        ProductDTO dto = ProductDTO.builder()
                .id(productId)
                .qrCode("QR-001")
                .name("Laptop Dell XPS 15")
                .detail("Intel Core i7, 16GB RAM, 512GB SSD")
                .build();

        when(productService.findById(productId)).thenReturn(dto);

        // When / Then
        mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId))
                .andExpect(jsonPath("$.qrCode").value("QR-001"))
                .andExpect(jsonPath("$.name").value("Laptop Dell XPS 15"))
                .andExpect(jsonPath("$.detail").value("Intel Core i7, 16GB RAM, 512GB SSD"));
    }
    @Test
    void findById_WhenProductDoesNotExist_Returns404() throws Exception {
        // Given
        Long productId = 99L;
        when(productService.findById(productId)).thenThrow(new ProductNotFoundException(productId));

        // When / Then
        mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Product not found with id: 99"));
    }
    
    @Test
    void save_WithValidDTO_ReturnsProductDTO() throws Exception {
    	// Given      
        ProductDTO productDto = ProductDTO.builder()
        		.id(1L)
                .qrCode("QR-001")
                .name("Laptop Dell XPS 15")
                .detail("Intel Core i7, 16GB RAM, 512GB SSD")
                .build();
        
        when(productService.save(any(ProductDTO.class))).thenReturn(productDto);
        
        // When / Then
        mockMvc.perform(post("/api/products")
    	        .contentType(MediaType.APPLICATION_JSON)  
    	        .content("""
    	                {
    	                  "qrCode": "QR-001",
    	                  "name": "Laptop Dell XPS 15",
    	                  "detail": "Intel Core i7, 16GB RAM, 512GB SSD"
    	                }
    	                """))
        		.andExpect(status().isCreated());
    }
    
    @Test
    void update_WhenProductExist_ReturnsUpdateProducDTO() throws Exception {
    	//Given
    	Long productId = 1L;
    	ProductDTO productDto = ProductDTO.builder()
                .qrCode("QR-002")
                .name("Laptop Dell")
                .detail("Intel Core i7, 16GB RAM, 2T SSD")
                .build();
    	
    	// lo que devuelve el repositorio después de guardar — datos nuevos
    	ProductDTO updatedProduct = ProductDTO.builder()
    	        .id(1L)
    	        .qrCode("QR-002")           // ← datos nuevos
    	        .name("Laptop Dell")
    	        .detail("Intel Core i7, 16GB RAM, 2T SSD")
    	        .build();
    	
    	when(productService.update(eq(productId), any(ProductDTO.class))).thenReturn(updatedProduct);
    	
    	// When / Then
    	mockMvc.perform(put("/api/products/" + productId)
    			.contentType(MediaType.APPLICATION_JSON)
    			.content("""
    	                {
    	                  "qrCode": "QR-002",
    	                  "name": "Laptop Dell",
    	                  "detail": "Intel Core i7, 16GB RAM, 2T SSD"
    	                }
    	                """))
    			.andExpect(status().isOk())
    			.andExpect(jsonPath("$.qrCode").value("QR-002"))
    	        .andExpect(jsonPath("$.name").value("Laptop Dell"));
    }

    
    @Test
    void delete_WhenProductExist_Returns204() throws Exception {
        // Given
        Long productId = 1L;

        // When / Then
        mockMvc.perform(delete("/api/products/{id}", productId))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteById(productId);
    }

    @Test
    void getMetrics_ReturnsAllCountersAtZero() throws Exception {
        // Given — singleton ya reseteado en @BeforeEach, no se mockea nada
        // productService es un @MockitoBean — nunca llama al ProductServiceImpl real
        // por lo tanto los contadores del singleton nunca se incrementan en estos tests

        // When / Then
        mockMvc.perform(get("/api/products/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.created").value(0))
                .andExpect(jsonPath("$.fetched").value(0))
                .andExpect(jsonPath("$.updated").value(0))
                .andExpect(jsonPath("$.deleted").value(0));
    }

}
