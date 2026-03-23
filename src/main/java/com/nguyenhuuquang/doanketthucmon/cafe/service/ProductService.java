package com.nguyenhuuquang.doanketthucmon.cafe.service;

import java.util.List;
import com.nguyenhuuquang.doanketthucmon.cafe.dto.request.ProductRequest;
import com.nguyenhuuquang.doanketthucmon.cafe.dto.response.ProductResponse;

public interface ProductService {
    ProductResponse createProduct(ProductRequest request);

    List<ProductResponse> getAllProducts();

    ProductResponse getProductById(Long id);

    ProductResponse updateProduct(Long id, ProductRequest request);

    void deleteProduct(Long id);
}
