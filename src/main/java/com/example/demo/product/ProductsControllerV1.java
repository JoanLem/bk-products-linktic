package com.example.demo.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.request.ProductRequest;
import com.example.demo.response.ProductResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Products", description = "API para gestión de productos")
public class ProductsControllerV1 {

	@Autowired
	private ProductServiceV1 productService;
	
	@Operation(summary = "Health check", description = "Verifica que la API esté funcionando correctamente")
	@ApiResponse(responseCode = "200", description = "API funcionando correctamente")
	@GetMapping("/health")
	public String health() {
		return "API products V1 is working";
	}
	
	@Operation(
		summary = "Crear un nuevo producto",
		description = "Crea un nuevo producto en el sistema con la información proporcionada"
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "201",
			description = "Producto creado exitosamente",
			content = @Content(schema = @Schema(implementation = ProductResponse.class))
		),
		@ApiResponse(
			responseCode = "400",
			description = "Datos de entrada inválidos",
			content = @Content
		)
	})
	@PostMapping
	public ResponseEntity<ProductResponse> createProduct(
			@Parameter(description = "Datos del producto a crear", required = true)
			@Valid @RequestBody ProductRequest request) {
		ProductResponse response = productService.create(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	@Operation(
		summary = "Obtener producto por ID",
		description = "Obtiene la información de un producto específico mediante su ID"
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "Producto encontrado",
			content = @Content(schema = @Schema(implementation = ProductResponse.class))
		),
		@ApiResponse(
			responseCode = "404",
			description = "Producto no encontrado",
			content = @Content
		)
	})
	@GetMapping("/{id}")
	public ResponseEntity<ProductResponse> getProductById(
			@Parameter(description = "ID del producto", required = true, example = "1")
			@PathVariable Long id) {
		ProductResponse response = productService.findById(id);
		return ResponseEntity.ok(response);
	}
	
	@Operation(
		summary = "Listar todos los productos",
		description = "Obtiene una lista de todos los productos registrados en el sistema"
	)
	@ApiResponse(
		responseCode = "200",
		description = "Lista de productos obtenida exitosamente",
		content = @Content(schema = @Schema(implementation = ProductResponse.class))
	)
	@GetMapping
	public ResponseEntity<List<ProductResponse>> getAllProducts() {
		List<ProductResponse> products = productService.findAll();
		return ResponseEntity.ok(products);
	}
}
