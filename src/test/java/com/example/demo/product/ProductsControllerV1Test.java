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

import com.example.demo.exception.GlobalExceptionHandler;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.request.ProductRequest;
import com.example.demo.response.ProductResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductsControllerV1 Tests")
class ProductsControllerV1Test {

    @Mock
    private ProductServiceV1 productService;

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
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();

        productRequest = new ProductRequest();
        productRequest.setName("Producto Test");
        productRequest.setPrice(new BigDecimal("99.99"));
        productRequest.setDescription("Descripción del producto");

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
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Producto Test"))
                .andExpect(jsonPath("$.price").value(99.99))
                .andExpect(jsonPath("$.description").value("Descripción del producto"));

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
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].field").exists())
                .andExpect(jsonPath("$[0].message").exists());

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
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Producto Test"));

        verify(productService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("GET /api/v1/products/{id} - Debería retornar 404 cuando el producto no existe")
    void shouldReturnNotFoundWhenProductDoesNotExist() throws Exception {
        // Given
        when(productService.findById(999L))
                .thenThrow(new ResourceNotFoundException("Producto no encontrado con ID: 999"));

        // When & Then
        mockMvc.perform(get("/api/v1/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.field").value("id"))
                .andExpect(jsonPath("$.message").value("Producto no encontrado con ID: 999"));

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
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Producto Test"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Producto 2"));

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

