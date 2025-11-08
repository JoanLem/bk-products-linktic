package com.example.demo.product;

import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.request.ProductRequest;
import com.example.demo.response.ProductResponse;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ProductService {

	private final ProductRepository repo;
	
	public ProductService(ProductRepository repo) {
		this.repo = repo;
	}
	
	
	
	/**
     * Crea un nuevo producto y devuelve la respuesta JSON:API
     */
	@Transactional
    public ProductResponse create(ProductRequest request) {
        ProductModel entity = new ProductModel();
        entity.setName(request.getName());
        entity.setPrice(request.getPrice());
        entity.setDescription(request.getDescription());
        entity.setStatus(request.getStatus());
        repo.save(entity);

        return toResponse(entity);
    }
    /**
     * Obtiene un producto por su ID, lanza excepción si no existe
     */
    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        ProductModel entity = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
        return toResponse(entity);
    }

    /**
     * Lista todos los productos registrados
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        return repo.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Convierte el modelo en un DTO de respuesta JSON:API
     */
    private ProductResponse toResponse(ProductModel entity) {
        return new ProductResponse(
                entity.getId(),
                entity.getName(),
                entity.getPrice(),
                entity.getDescription()
        );
    }
	
}
