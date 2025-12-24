package com.nguyenhuuquang.doanketthucmon.cafe.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nguyenhuuquang.doanketthucmon.cafe.dto.ProductRequest;
import com.nguyenhuuquang.doanketthucmon.cafe.dto.ProductResponse;
import com.nguyenhuuquang.doanketthucmon.cafe.entity.Category;
import com.nguyenhuuquang.doanketthucmon.cafe.entity.Product;
import com.nguyenhuuquang.doanketthucmon.cafe.repository.CategoryRepository;
import com.nguyenhuuquang.doanketthucmon.cafe.repository.ProductRepository;
import com.nguyenhuuquang.doanketthucmon.cafe.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }

    // ✅ Đổi đường dẫn thành /upload và lưu vào static/uploads/
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<Product> createProductWithImage(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") BigDecimal price,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "stockQuantity", required = false, defaultValue = "0") Integer stockQuantity,
            @RequestParam(value = "isActive", required = false, defaultValue = "true") Boolean isActive,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) throws IOException {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        String imageUrl = null;

        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            Path uploadPath = Paths.get("src/main/resources/static/uploads");

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            imageUrl = fileName;
        }

        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setCategory(category);
        product.setStockQuantity(stockQuantity);
        product.setIsActive(isActive);
        product.setImageUrl(imageUrl);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        productRepository.save(product);
        return ResponseEntity.ok(product);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<Product> updateProductWithImage(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") BigDecimal price,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "stockQuantity", required = false, defaultValue = "0") Integer stockQuantity,
            @RequestParam(value = "isActive", required = false, defaultValue = "true") Boolean isActive,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) throws IOException {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm!"));

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setCategory(category);
        product.setStockQuantity(stockQuantity);
        product.setIsActive(isActive);
        product.setUpdatedAt(LocalDateTime.now());

        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            Path uploadPath = Paths.get("src/main/resources/static/uploads");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            product.setImageUrl(fileName);
        }

        productRepository.save(product);
        return ResponseEntity.ok(product);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
