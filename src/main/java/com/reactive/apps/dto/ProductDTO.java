package com.reactive.apps.dto;

import com.reactive.apps.model.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long id;
    private String qrCode;
    private String name;
    private String detail;

    public static ProductDTO fromEntity(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .qrCode(product.getQrCode())
                .name(product.getName())
                .detail(product.getDetail())
                .build();
    }

    public Product toEntity() {
        return Product.builder()
                .id(this.id)
                .qrCode(this.qrCode)
                .name(this.name)
                .detail(this.detail)
                .build();
    }

}
