package com.example.ecommercebackend.service.impl;

import com.example.ecommercebackend.dto.CategoryDTO;
import com.example.ecommercebackend.dto.CategoryResponse;
import com.example.ecommercebackend.dto.ProductDTO;
import com.example.ecommercebackend.dto.ProductResponse;
import com.example.ecommercebackend.exception.custom.ResourceNotFoundException;
import com.example.ecommercebackend.model.Category;
import com.example.ecommercebackend.model.Product;
import com.example.ecommercebackend.repository.CategoryRepository;
import com.example.ecommercebackend.repository.ProductRepository;
import com.example.ecommercebackend.service.CategoryService;
import com.example.ecommercebackend.service.ProductService;
import lombok.val;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    private ModelMapper modelMapper;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ProductResponse fetchProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepository.findAll(pageable);
        Page<ProductDTO> productDTOPage = productPage.map(product -> modelMapper.map(product, ProductDTO.class));
        return new ProductResponse(
                productDTOPage.getContent(),
                productDTOPage.getNumber(),
                productDTOPage.getSize(),
                productDTOPage.getTotalElements(),
                productDTOPage.getTotalPages(),
                productDTOPage.isLast()
        );
    }

    @Override
    public ProductDTO createProduct(ProductDTO productDTO, Long categoryId) {
        val category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException(String.format("Category with ID: %d not found", categoryId)));
        Optional<Product> existing = productRepository.findByProductName(productDTO.getProductName());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Product with name '" + productDTO.getProductName() + "' already exists");
        }
        val product = modelMapper.map(productDTO, Product.class);
        product.setProductImage("default.png");
        product.setCategory(category);
        val specialPrice = product.getPrice() - (
                (product.getDiscount() * 0.01) * product.getPrice()
        );
        product.setSpecialPrice(specialPrice);
        val savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    /*@Override
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    existingProduct.setProductName(productDTO.getProductName());
                    Product updatedProduct = productRepository.save(existingProduct);
                    return modelMapper.map(updatedProduct, ProductDTO.class);
                })
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }*/

    @Override
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        var product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        Optional.ofNullable(productDTO.getProductName())
                .filter(name -> !name.isBlank())
                .ifPresent(product::setProductName);

        Optional.ofNullable(productDTO.getProductImage())
                .filter(image -> !image.isBlank())
                .ifPresent(product::setProductImage);

        Optional.ofNullable(productDTO.getDescription())
                .filter(desc -> !desc.isBlank())
                .ifPresent(product::setDescription);

        Optional.ofNullable(productDTO.getQuantity())
                .ifPresent(product::setQuantity);

        Optional.ofNullable(productDTO.getPrice())
                .ifPresent(product::setPrice);

        Optional.ofNullable(productDTO.getDiscount())
                .ifPresent(product::setDiscount);

        // Recalculate special price if price/discount changed
        if (product.getPrice() != null && product.getDiscount() != null) {
            var specialPrice = product.getPrice() - (product.getDiscount() * 0.01 * product.getPrice());
            product.setSpecialPrice(specialPrice);
        }

        var saved = productRepository.save(product);
        return modelMapper.map(saved, ProductDTO.class);
    }


    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    @Override
    public ProductResponse fetchProductsByCategory(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder, Long categoryId) {
        val categoryOptional = categoryRepository.findById(categoryId);
        if (categoryOptional.isEmpty())
            throw new ResourceNotFoundException(String.format("Category does not exist!"));
        val productsByCategoryId = productRepository.findByCategory_CategoryId(categoryId);
        val productDTOList = productsByCategoryId.stream().map(product -> modelMapper.map(product, ProductDTO.class)).collect(Collectors.toList());
        return new ProductResponse(productDTOList, null, null, null, null, null);
    }

    @Override
    public ProductResponse searchProduct(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder, String keyword) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        val searchedProducts = productRepository.findByProductNameContainingIgnoreCase(keyword, pageable);
        val productDTOList = searchedProducts.map(products -> modelMapper.map(products, ProductDTO.class));
        return new ProductResponse(
                productDTOList.getContent(),
                productDTOList.getNumber(),
                productDTOList.getSize(),
                productDTOList.getTotalElements(),
                productDTOList.getTotalPages(),
                productDTOList.isLast()
        );
    }
}
