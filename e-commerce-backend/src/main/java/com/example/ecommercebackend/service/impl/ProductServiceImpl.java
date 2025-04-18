package com.example.ecommercebackend.service.impl;

import com.example.ecommercebackend.dto.ProductDTO;
import com.example.ecommercebackend.dto.ProductResponse;
import com.example.ecommercebackend.exception.custom.ResourceNotFoundException;
import com.example.ecommercebackend.model.Category;
import com.example.ecommercebackend.model.Product;
import com.example.ecommercebackend.repository.CategoryRepository;
import com.example.ecommercebackend.repository.ProductRepository;
import com.example.ecommercebackend.service.FileService;
import com.example.ecommercebackend.service.ProductService;
import lombok.val;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final FileService fileService;
    @Value("${product.image.upload.path}")
    private String imageUploadPath;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, ModelMapper modelMapper, FileService fileService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
        this.fileService = fileService;
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
        Product product = modelMapper.map(productDTO, Product.class);
        product.setProductImage("default.png");
        product.setCategory(category);
        Double specialPrice = product.getPrice() - (
                (product.getDiscount() * 0.01) * product.getPrice()
        );
        product.setSpecialPrice(specialPrice);
        Product savedProduct = productRepository.save(product);
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
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        if (categoryOptional.isEmpty())
            throw new ResourceNotFoundException("Category does not exist!");

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productsByCategoryId = productRepository.findByCategory_CategoryId(categoryId, pageable);
        Page<ProductDTO> productDTOPageList = productsByCategoryId.map(product -> modelMapper.map(product, ProductDTO.class));
        return new ProductResponse(
                productDTOPageList.getContent(),
                productDTOPageList.getNumber(),
                productDTOPageList.getSize(),
                productDTOPageList.getTotalElements(),
                productDTOPageList.getTotalPages(),
                productDTOPageList.isLast());
    }

    @Override
    public ProductResponse searchProduct(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder, String keyword) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> searchedProducts = productRepository.findByProductNameContainingIgnoreCase(keyword, pageable);
        Page<ProductDTO> productDTOList = searchedProducts.map(products -> modelMapper.map(products, ProductDTO.class));
        return new ProductResponse(
                productDTOList.getContent(),
                productDTOList.getNumber(),
                productDTOList.getSize(),
                productDTOList.getTotalElements(),
                productDTOList.getTotalPages(),
                productDTOList.isLast()
        );
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile productImage) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Product not found with id: ", productId)));

        // 1. Get original file name
        String originalFilename = productImage.getOriginalFilename();

        // 2. Generate unique file name
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        String fileName = "product_" + product.getProductName() + "_" + System.currentTimeMillis() + fileExtension;

        // 3. Create full path
        Path uploadPath = Paths.get(imageUploadPath);
        Path filePath = uploadPath.resolve(fileName);

        try {
            // 4. Ensure upload dir exists
            Files.createDirectories(uploadPath);

            // 5. Save the file
            Files.copy(productImage.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 6. Update product entity
            product.setProductImage(fileName);

            Product updatedProduct = productRepository.save(product);
            return modelMapper.map(updatedProduct, ProductDTO.class);

        } catch (Exception e) {
            throw new RuntimeException("Error uploading product image", e);
        }
    }

    /*@Override
    public ProductDTO updateProductImage(Long productId, MultipartFile productImage) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        try {
            String fileName = fileService.uploadFile(productImage, imageUploadPath);
            product.setProductImage(fileName);
            Product updatedProduct = productRepository.save(product);

            return modelMapper.map(updatedProduct, ProductDTO.class);

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image", e);
        }
    }*/

}
