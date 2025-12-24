package com.nguyenhuuquang.doanketthucmon.cafe.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nguyenhuuquang.doanketthucmon.cafe.dto.BillRequest;
import com.nguyenhuuquang.doanketthucmon.cafe.dto.BillResponse;
import com.nguyenhuuquang.doanketthucmon.cafe.dto.CategoryResponse;
import com.nguyenhuuquang.doanketthucmon.cafe.dto.OrderItemResponse;
import com.nguyenhuuquang.doanketthucmon.cafe.dto.OrderResponse;
import com.nguyenhuuquang.doanketthucmon.cafe.dto.ProductResponse;
import com.nguyenhuuquang.doanketthucmon.cafe.dto.TableResponse;
import com.nguyenhuuquang.doanketthucmon.cafe.dto.UserResponse;
import com.nguyenhuuquang.doanketthucmon.cafe.entity.Bill;
import com.nguyenhuuquang.doanketthucmon.cafe.entity.Order;
import com.nguyenhuuquang.doanketthucmon.cafe.repository.BillRepository;
import com.nguyenhuuquang.doanketthucmon.cafe.repository.OrderRepository;
import com.nguyenhuuquang.doanketthucmon.cafe.service.BillService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BillServiceImpl implements BillService {

    private final BillRepository billRepository;
    private final OrderRepository orderRepository;

    @Override
    public BillResponse createBill(BillRequest request) {
        Bill bill = mapRequestToEntity(request, new Bill());
        billRepository.save(bill);
        return mapToResponse(bill);
    }

    @Override
    public BillResponse updateBill(Long id, BillRequest request) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bill not found"));
        bill = mapRequestToEntity(request, bill);
        billRepository.save(bill);
        return mapToResponse(bill);
    }

    @Override
    public BillResponse getBillById(Long id) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bill not found"));
        return mapToResponse(bill);
    }

    @Override
    public List<BillResponse> getAllBills() {
        return billRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteBill(Long id) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bill not found"));
        billRepository.delete(bill);
    }

    private Bill mapRequestToEntity(BillRequest request, Bill bill) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        bill.setOrder(order);
        bill.setTotalAmount(request.getTotalAmount());
        bill.setPaymentMethod(request.getPaymentMethod());
        bill.setPaymentStatus(request.getPaymentStatus());
        bill.setIssuedAt(request.getIssuedAt());
        bill.setNotes(request.getNotes());

        if (bill.getCreatedAt() == null) {
            bill.setCreatedAt(LocalDateTime.now());
        }
        bill.setUpdatedAt(LocalDateTime.now());

        return bill;
    }

    private BillResponse mapToResponse(Bill bill) {
        Order order = bill.getOrder();

        OrderResponse orderResponse = OrderResponse.builder()
                .id(order.getId())
                .table(order.getTable() != null ? TableResponse.builder()
                        .id(order.getTable().getId())
                        .number(order.getTable().getNumber())
                        .capacity(order.getTable().getCapacity())
                        .status(order.getTable().getStatus())
                        .createdAt(order.getTable().getCreatedAt())
                        .updatedAt(order.getTable().getUpdatedAt())
                        .build()
                        : null)
                .employee(order.getEmployee() != null ? UserResponse.builder()
                        .id(order.getEmployee().getId())
                        .username(order.getEmployee().getUsername())
                        .fullName(order.getEmployee().getFullName())
                        .role(order.getEmployee().getRole())
                        .email(order.getEmployee().getEmail())
                        .phone(order.getEmployee().getPhone())
                        .imageUrl(order.getEmployee().getImageUrl())
                        .isActive(order.getEmployee().getIsActive())
                        .build()
                        : null)

                .status(order.getStatus() != null ? order.getStatus().name() : null)
                .totalAmount(order.getTotalAmount())
                .notes(order.getNotes())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .items(order.getItems() != null ? order.getItems().stream().map(item -> OrderItemResponse.builder()
                        .id(item.getId())
                        .product(ProductResponse.builder()
                                .id(item.getProduct().getId())
                                .name(item.getProduct().getName())
                                .description(item.getProduct().getDescription())
                                .price(item.getProduct().getPrice())
                                .category(item.getProduct().getCategory() != null ? CategoryResponse.builder()
                                        .id(item.getProduct().getCategory().getId())
                                        .name(item.getProduct().getCategory().getName())
                                        .description(item.getProduct().getCategory().getDescription())
                                        .imageUrl(item.getProduct().getCategory().getImageUrl())
                                        .createdAt(item.getProduct().getCategory().getCreatedAt())
                                        .updatedAt(item.getProduct().getCategory().getUpdatedAt())
                                        .build()
                                        : null)
                                .imageUrl(item.getProduct().getImageUrl())
                                .stockQuantity(item.getProduct().getStockQuantity())
                                .isActive(item.getProduct().getIsActive())
                                .build())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .subtotal(item.getSubtotal())
                        .createdAt(item.getCreatedAt())
                        .updatedAt(item.getUpdatedAt())
                        .build()).toList() : null)
                .build();

        return BillResponse.builder()
                .id(bill.getId())
                .order(orderResponse)
                .totalAmount(bill.getTotalAmount())
                .paymentMethod(bill.getPaymentMethod())
                .paymentStatus(bill.getPaymentStatus())
                .issuedAt(bill.getIssuedAt())
                .notes(bill.getNotes())
                .createdAt(bill.getCreatedAt())
                .updatedAt(bill.getUpdatedAt())
                .build();
    }

}
