package com.example.demo.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequest {
	
	@NotBlank(message = "El nombre del producto es obligatorio")
    private String name;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor que cero")
    private BigDecimal price;

    private String description;
    
    private Boolean status;

    public ProductRequest() {}

    public ProductRequest(String name, BigDecimal price, String description) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.status = true;
    }
}
