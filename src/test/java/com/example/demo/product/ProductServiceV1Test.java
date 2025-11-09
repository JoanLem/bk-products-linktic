package com.example.demo.product;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.request.ProductRequest;
import com.example.demo.response.ProductResponse;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Tests")
class ProductServiceV1Test {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceV1 productService;

    private ProductModel productModel;
    private ProductRequest productRequest;

    @BeforeEach
    void setUp() {
        productModel = new ProductModel();
        productModel.setId(1L);
        productModel.setName("Producto Test");
        productModel.setPrice(new BigDecimal("99.99"));
        productModel.setDescription("Descripción del producto");
        productModel.setStatus(true);

        productRequest = new ProductRequest();
        productRequest.setName("Producto Test");
        productRequest.setPrice(new BigDecimal("99.99"));
        productRequest.setDescription("Descripción del producto");
        productRequest.setStatus(true);
    }

    @Test
    @DisplayName("Debería crear un producto exitosamente")
    void shouldCreateProductSuccessfully() {
        // Given
        // Usamos thenAnswer para asignar el ID al objeto que se pasa al mock
        when(productRepository.save(any(ProductModel.class))).thenAnswer(invocation -> {
            ProductModel entity = invocation.getArgument(0);
            entity.setId(1L);
            return entity;
        });

        // When
        ProductResponse response = productService.create(productRequest);

        // Then
        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals("product", response.getData().getType());
        assertEquals(1L, response.getData().getAttributes().getId());
        assertEquals("Producto Test", response.getData().getAttributes().getName());
        assertEquals(new BigDecimal("99.99"), response.getData().getAttributes().getPrice());
        assertEquals("Descripción del producto", response.getData().getAttributes().getDescription());

        verify(productRepository, times(1)).save(any(ProductModel.class));
    }

    @Test
    @DisplayName("Debería encontrar un producto por ID")
    void shouldFindProductById() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(productModel));

        // When
        ProductResponse response = productService.findById(1L);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getData().getAttributes().getId());
        assertEquals("Producto Test", response.getData().getAttributes().getName());

        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException cuando el producto no existe")
    void shouldThrowResourceNotFoundExceptionWhenProductNotFound() {
        // Given
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> productService.findById(999L)
        );

        assertEquals("Producto no encontrado con ID: 999", exception.getMessage());
        verify(productRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Debería listar todos los productos")
    void shouldFindAllProducts() {
        // Given
        ProductModel product2 = new ProductModel();
        product2.setId(2L);
        product2.setName("Producto 2");
        product2.setPrice(new BigDecimal("149.99"));
        product2.setDescription("Descripción 2");
        product2.setStatus(true);

        List<ProductModel> products = Arrays.asList(productModel, product2);
        when(productRepository.findAll()).thenReturn(products);

        // When
        List<ProductResponse> responses = productService.findAll();

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals(1L, responses.get(0).getData().getAttributes().getId());
        assertEquals(2L, responses.get(1).getData().getAttributes().getId());

        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería retornar lista vacía cuando no hay productos")
    void shouldReturnEmptyListWhenNoProducts() {
        // Given
        when(productRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<ProductResponse> responses = productService.findAll();

        // Then
        assertNotNull(responses);
        assertTrue(responses.isEmpty());

        verify(productRepository, times(1)).findAll();
    }
}

