package com.example.ecommercebackend.controller;

import com.example.ecommercebackend.dto.CategoryDTO;
import com.example.ecommercebackend.dto.CategoryResponse;
import com.example.ecommercebackend.dto.ProductDTO;
import com.example.ecommercebackend.dto.ProductResponse;
import com.example.ecommercebackend.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.ecommercebackend.config.AppConstants.*;
import static com.example.ecommercebackend.config.AppConstants.SORT_DIR;

@RestController
@RequestMapping("products")
public class ProductController {

    private ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<ProductResponse> getProductsWthPagination(@RequestParam(value = "pageNumber", defaultValue = PAGE_NUMBER, required = false) Integer pageNumber, @RequestParam(value = "pageSize", defaultValue = PAGE_SIZE, required = false) Integer pageSize, @RequestParam(value = "sortBy", defaultValue = PRODUCT_SORT_BY, required = false) String sortBy, @RequestParam(value = "sortOrder", defaultValue = SORT_DIR, required = false) String sortOrder) {
        return new ResponseEntity<>(productService.fetchProducts(pageNumber, pageSize, sortBy, sortOrder), HttpStatus.OK);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<ProductResponse> getProductsByCategoryWthPagination(@RequestParam(value = "pageNumber", defaultValue = PAGE_NUMBER, required = false) Integer pageNumber, @RequestParam(value = "pageSize", defaultValue = PAGE_SIZE, required = false) Integer pageSize, @RequestParam(value = "sortBy", defaultValue = PRODUCT_SORT_BY, required = false) String sortBy, @RequestParam(value = "sortOrder", defaultValue = SORT_DIR, required = false) String sortOrder, @PathVariable Long categoryId) {
        return new ResponseEntity<>(productService.fetchProductsByCategory(pageNumber, pageSize, sortBy, sortOrder, categoryId), HttpStatus.OK);
    }

    @GetMapping("/search/{keyword}")
    public ResponseEntity<ProductResponse> searchProduct(@RequestParam(value = "pageNumber", defaultValue = PAGE_NUMBER, required = false) Integer pageNumber, @RequestParam(value = "pageSize", defaultValue = PAGE_SIZE, required = false) Integer pageSize, @RequestParam(value = "sortBy", defaultValue = PRODUCT_SORT_BY, required = false) String sortBy, @RequestParam(value = "sortOrder", defaultValue = SORT_DIR, required = false) String sortOrder, @PathVariable String keyword) {
        return new ResponseEntity<>(productService.searchProduct(pageNumber, pageSize, sortBy, sortOrder, keyword), HttpStatus.OK);
    }

    @PostMapping(value = {"/admin/{categoryId}"})
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO productDTO, @PathVariable Long categoryId) {
        return ResponseEntity.status(201).body(productService.createProduct(productDTO, categoryId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        return ResponseEntity.ok(productService.updateProduct(id, productDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
