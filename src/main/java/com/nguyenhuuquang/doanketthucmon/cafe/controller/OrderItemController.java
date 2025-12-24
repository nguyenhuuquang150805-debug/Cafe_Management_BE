package com.nguyenhuuquang.doanketthucmon.cafe.controller;

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
import org.springframework.web.bind.annotation.RestController;

import com.nguyenhuuquang.doanketthucmon.cafe.dto.OrderItemRequest;
import com.nguyenhuuquang.doanketthucmon.cafe.dto.OrderItemResponse;
import com.nguyenhuuquang.doanketthucmon.cafe.service.OrderItemService;

import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/order-items")
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemService orderItemService;

    @PostMapping
    public ResponseEntity<OrderItemResponse> create(@RequestBody OrderItemRequest request) {
        return ResponseEntity.ok(orderItemService.createOrderItem(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderItemResponse> update(@PathVariable Long id, @RequestBody OrderItemRequest request) {
        return ResponseEntity.ok(orderItemService.updateOrderItem(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderItemResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderItemService.getOrderItemById(id));
    }

    @GetMapping
    public ResponseEntity<List<OrderItemResponse>> getAll() {
        return ResponseEntity.ok(orderItemService.getAllOrderItems());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderItemService.deleteOrderItem(id);
        return ResponseEntity.noContent().build();
    }
}
