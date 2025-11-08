package com.example.demo.product;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.request.ProductRequest;
import com.example.demo.response.ProductResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductsControllerV1 Tests")
class ProductsControllerV1Test {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductsControllerV1 productsController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private ProductRequest productRequest;
    private ProductResponse productResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(productsController)
                .build();

        objectMapper = new ObjectMapper();

        productRequest = new ProductRequest();
        productRequest.setName("Producto Test");
        productRequest.setPrice(new BigDecimal("99.99"));
        productRequest.setDescription("Descripción del producto");
        productRequest.setStatus(true);

        productResponse = new ProductResponse(
            1L,
            "Producto Test",
            new BigDecimal("99.99"),
            "Descripción del producto"
        );
    }

    @Test
    @DisplayName("GET /health - Debería retornar mensaje de salud")
    void shouldReturnHealthCheck() throws Exception {
        mockMvc.perform(get("/api/v1/products/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("API products V1 is working"));
    }

    @Test
    @DisplayName("POST /api/v1/products - Debería crear un producto exitosamente")
    void shouldCreateProductSuccessfully() throws Exception {
        // Given
        when(productService.create(any(ProductRequest.class))).thenReturn(productResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.type").value("product"))
                .andExpect(jsonPath("$.data.attributes.id").value(1L))
                .andExpect(jsonPath("$.data.attributes.name").value("Producto Test"))
                .andExpect(jsonPath("$.data.attributes.price").value(99.99))
                .andExpect(jsonPath("$.data.attributes.description").value("Descripción del producto"));

        verify(productService, times(1)).create(any(ProductRequest.class));
    }

    @Test
    @DisplayName("POST /api/v1/products - Debería retornar 400 cuando los datos son inválidos")
    void shouldReturnBadRequestWhenInvalidData() throws Exception {
        // Given
        ProductRequest invalidRequest = new ProductRequest();
        invalidRequest.setName(""); // Nombre vacío
        invalidRequest.setPrice(null); // Precio nulo

        // When & Then
        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(productService, never()).create(any(ProductRequest.class));
    }

    @Test
    @DisplayName("GET /api/v1/products/{id} - Debería obtener un producto por ID")
    void shouldGetProductById() throws Exception {
        // Given
        when(productService.findById(1L)).thenReturn(productResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/products/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.attributes.id").value(1L))
                .andExpect(jsonPath("$.data.attributes.name").value("Producto Test"));

        verify(productService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("GET /api/v1/products/{id} - Debería retornar 500 cuando el producto no existe")
    void shouldReturnInternalServerErrorWhenProductDoesNotExist() throws Exception {
        // Given
        when(productService.findById(999L))
                .thenThrow(new ResourceNotFoundException("Producto no encontrado con ID: 999"));

        // When & Then
        // Sin GlobalExceptionHandler, Spring Boot devuelve 500 Internal Server Error
        mockMvc.perform(get("/api/v1/products/999"))
                .andExpect(status().isInternalServerError());

        verify(productService, times(1)).findById(999L);
    }

    @Test
    @DisplayName("GET /api/v1/products - Debería listar todos los productos")
    void shouldGetAllProducts() throws Exception {
        // Given
        ProductResponse product2 = new ProductResponse(
            2L,
            "Producto 2",
            new BigDecimal("149.99"),
            "Descripción 2"
        );

        List<ProductResponse> products = Arrays.asList(productResponse, product2);
        when(productService.findAll()).thenReturn(products);

        // When & Then
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].data.attributes.id").value(1L))
                .andExpect(jsonPath("$[1].data.attributes.id").value(2L));

        verify(productService, times(1)).findAll();
    }

    @Test
    @DisplayName("GET /api/v1/products - Debería retornar lista vacía cuando no hay productos")
    void shouldReturnEmptyListWhenNoProducts() throws Exception {
        // Given
        when(productService.findAll()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(productService, times(1)).findAll();
    }
}

