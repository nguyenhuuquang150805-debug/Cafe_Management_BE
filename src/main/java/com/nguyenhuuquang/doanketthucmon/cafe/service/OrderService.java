package com.nguyenhuuquang.doanketthucmon.cafe.service;

import java.util.List;

import com.nguyenhuuquang.doanketthucmon.cafe.dto.request.OrderRequest;
import com.nguyenhuuquang.doanketthucmon.cafe.dto.response.OrderResponse;

public interface OrderService {
    OrderResponse createOrder(OrderRequest request);

    List<OrderResponse> getAllOrders();

    OrderResponse getOrderById(Long id);

    OrderResponse updateOrder(Long id, OrderRequest request);

    void deleteOrder(Long id);
}
