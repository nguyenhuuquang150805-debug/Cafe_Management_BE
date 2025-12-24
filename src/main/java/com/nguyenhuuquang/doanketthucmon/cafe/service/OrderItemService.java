package com.nguyenhuuquang.doanketthucmon.cafe.service;

import java.util.List;

import com.nguyenhuuquang.doanketthucmon.cafe.dto.OrderItemRequest;
import com.nguyenhuuquang.doanketthucmon.cafe.dto.OrderItemResponse;

public interface OrderItemService {
    OrderItemResponse createOrderItem(OrderItemRequest request);

    OrderItemResponse updateOrderItem(Long id, OrderItemRequest request);

    OrderItemResponse getOrderItemById(Long id);

    List<OrderItemResponse> getAllOrderItems();

    void deleteOrderItem(Long id);
}
