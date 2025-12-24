package com.nguyenhuuquang.doanketthucmon.cafe.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nguyenhuuquang.doanketthucmon.cafe.dto.CategoryResponse;
import com.nguyenhuuquang.doanketthucmon.cafe.dto.OrderItemResponse;
import com.nguyenhuuquang.doanketthucmon.cafe.dto.OrderRequest;
import com.nguyenhuuquang.doanketthucmon.cafe.dto.OrderResponse;
import com.nguyenhuuquang.doanketthucmon.cafe.dto.ProductResponse;
import com.nguyenhuuquang.doanketthucmon.cafe.dto.PromotionResponse;
import com.nguyenhuuquang.doanketthucmon.cafe.dto.TableResponse;
import com.nguyenhuuquang.doanketthucmon.cafe.dto.UserResponse;
import com.nguyenhuuquang.doanketthucmon.cafe.entity.Order;
import com.nguyenhuuquang.doanketthucmon.cafe.entity.OrderItem;
import com.nguyenhuuquang.doanketthucmon.cafe.entity.Product;
import com.nguyenhuuquang.doanketthucmon.cafe.entity.Promotion;
import com.nguyenhuuquang.doanketthucmon.cafe.entity.TableEntity;
import com.nguyenhuuquang.doanketthucmon.cafe.entity.User;
import com.nguyenhuuquang.doanketthucmon.cafe.entity.enums.OrderStatus;
import com.nguyenhuuquang.doanketthucmon.cafe.repository.OrderRepository;
import com.nguyenhuuquang.doanketthucmon.cafe.repository.ProductRepository;
import com.nguyenhuuquang.doanketthucmon.cafe.repository.PromotionRepository;
import com.nguyenhuuquang.doanketthucmon.cafe.repository.TableRepository;
import com.nguyenhuuquang.doanketthucmon.cafe.repository.UserRepository;
import com.nguyenhuuquang.doanketthucmon.cafe.service.OrderService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

        private final OrderRepository orderRepository;
        private final TableRepository tableRepository;
        private final UserRepository userRepository;
        private final PromotionRepository promotionRepository;
        private final ProductRepository productRepository;

        @Override
        public OrderResponse createOrder(OrderRequest request) {
                TableEntity table = null;
                if (request.getTableId() != null) {
                        table = tableRepository.findById(request.getTableId())
                                        .orElseThrow(() -> new RuntimeException("Table not found"));
                }
                User employee = null;
                if (request.getEmployeeId() != null) {
                        employee = userRepository.findById(request.getEmployeeId())
                                        .orElseThrow(() -> new RuntimeException("Employee not found"));
                }

                Promotion promotion = null;
                if (request.getPromotionId() != null) {
                        promotion = promotionRepository.findById(request.getPromotionId())
                                        .orElseThrow(() -> new RuntimeException("Promotion not found"));
                }

                Order order = new Order();
                order.setTable(table);
                order.setEmployee(employee);
                order.setStatus(OrderStatus.valueOf(request.getStatus()));
                order.setTotalAmount(request.getTotalAmount());
                order.setPromotion(promotion);
                order.setNotes(request.getNotes());
                order.setCreatedAt(LocalDateTime.now());
                order.setUpdatedAt(LocalDateTime.now());

                if (request.getItems() != null) {
                        List<OrderItem> items = request.getItems().stream().map(itemReq -> {
                                Product product = productRepository.findById(itemReq.getProductId())
                                                .orElseThrow(() -> new RuntimeException("Product not found"));

                                OrderItem item = new OrderItem();
                                item.setOrder(order);
                                item.setProduct(product);
                                item.setQuantity(itemReq.getQuantity());
                                item.setPrice(itemReq.getPrice());
                                return item;
                        }).collect(Collectors.toList());

                        order.setItems(items);
                }

                orderRepository.save(order);
                return mapToResponse(order);
        }

        @Override
        public List<OrderResponse> getAllOrders() {
                return orderRepository.findAll().stream()
                                .map(this::mapToResponse)
                                .collect(Collectors.toList());
        }

        @Override
        public OrderResponse getOrderById(Long id) {
                Order order = orderRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Order not found"));
                return mapToResponse(order);
        }

        @Override
        public OrderResponse updateOrder(Long id, OrderRequest request) {
                Order order = orderRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Order not found"));

                if (request.getTableId() != null) {
                        TableEntity table = tableRepository.findById(request.getTableId())
                                        .orElseThrow(() -> new RuntimeException("Table not found"));
                        order.setTable(table);
                } else {
                        order.setTable(null);
                }
                if (request.getEmployeeId() != null) {
                        User employee = userRepository.findById(request.getEmployeeId())
                                        .orElseThrow(() -> new RuntimeException("Employee not found"));
                        order.setEmployee(employee);
                }

                if (request.getPromotionId() != null) {
                        Promotion promotion = promotionRepository.findById(request.getPromotionId())
                                        .orElseThrow(() -> new RuntimeException("Promotion not found"));
                        order.setPromotion(promotion);
                }

                // ✅ Cập nhật status
                if (request.getStatus() != null) {
                        order.setStatus(OrderStatus.valueOf(request.getStatus()));
                }

                // ✅ Cập nhật totalAmount
                if (request.getTotalAmount() != null) {
                        order.setTotalAmount(request.getTotalAmount());
                }

                // ✅ Cập nhật notes
                if (request.getNotes() != null) {
                        order.setNotes(request.getNotes());
                }

                order.setUpdatedAt(LocalDateTime.now());

                // ✅ CHỈ cập nhật items nếu request có items
                if (request.getItems() != null && !request.getItems().isEmpty()) {
                        order.getItems().clear();
                        List<OrderItem> items = request.getItems().stream().map(itemReq -> {
                                Product product = productRepository.findById(itemReq.getProductId())
                                                .orElseThrow(() -> new RuntimeException("Product not found"));

                                OrderItem item = new OrderItem();
                                item.setOrder(order);
                                item.setProduct(product);
                                item.setQuantity(itemReq.getQuantity());
                                item.setPrice(itemReq.getPrice());
                                return item;
                        }).collect(Collectors.toList());

                        order.setItems(items);
                }

                orderRepository.save(order);
                return mapToResponse(order);
        }

        @Override
        public void deleteOrder(Long id) {
                Order order = orderRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Order not found"));
                orderRepository.delete(order);
        }

        private OrderResponse mapToResponse(Order order) {
                return OrderResponse.builder()
                                .id(order.getId())
                                .table(order.getTable() != null
                                                ? TableResponse.builder()
                                                                .id(order.getTable().getId())
                                                                .number(order.getTable().getNumber())
                                                                .capacity(order.getTable().getCapacity())
                                                                .status(order.getTable().getStatus())
                                                                .createdAt(order.getTable().getCreatedAt())
                                                                .updatedAt(order.getTable().getUpdatedAt())
                                                                .build()
                                                : null)
                                .employee(order.getEmployee() != null
                                                ? UserResponse.builder()
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
                                .status(order.getStatus().name())
                                .totalAmount(order.getTotalAmount())
                                .promotion(order.getPromotion() != null
                                                ? PromotionResponse.builder()
                                                                .id(order.getPromotion().getId())
                                                                .name(order.getPromotion().getName())
                                                                .discountPercentage(order.getPromotion()
                                                                                .getDiscountPercentage())
                                                                .discountAmount(order.getPromotion()
                                                                                .getDiscountAmount())
                                                                .startDate(order.getPromotion().getStartDate())
                                                                .endDate(order.getPromotion().getEndDate())
                                                                .isActive(order.getPromotion().getIsActive())
                                                                .createdAt(order.getPromotion().getCreatedAt())
                                                                .updatedAt(order.getPromotion().getUpdatedAt())
                                                                .products(order.getPromotion().getProducts() != null
                                                                                ? order.getPromotion().getProducts()
                                                                                                .stream()
                                                                                                .map(p -> ProductResponse
                                                                                                                .builder()
                                                                                                                .id(p.getId())
                                                                                                                .name(p.getName())
                                                                                                                .description(p.getDescription())
                                                                                                                .price(p.getPrice())
                                                                                                                .category(p.getCategory() != null
                                                                                                                                ? CategoryResponse
                                                                                                                                                .builder()
                                                                                                                                                .id(p.getCategory()
                                                                                                                                                                .getId())
                                                                                                                                                .name(p.getCategory()
                                                                                                                                                                .getName())
                                                                                                                                                .description(p.getCategory()
                                                                                                                                                                .getDescription())
                                                                                                                                                .imageUrl(p.getCategory()
                                                                                                                                                                .getImageUrl())
                                                                                                                                                .createdAt(p.getCategory()
                                                                                                                                                                .getCreatedAt())
                                                                                                                                                .updatedAt(p.getCategory()
                                                                                                                                                                .getUpdatedAt())
                                                                                                                                                .build()
                                                                                                                                : null)
                                                                                                                .imageUrl(p.getImageUrl())
                                                                                                                .stockQuantity(p.getStockQuantity())
                                                                                                                .isActive(p.getIsActive())
                                                                                                                .build())
                                                                                                .collect(Collectors
                                                                                                                .toSet())
                                                                                : null)
                                                                .build()
                                                : null)
                                .notes(order.getNotes())
                                .createdAt(order.getCreatedAt())
                                .updatedAt(order.getUpdatedAt())
                                .items(order.getItems().stream().map(item -> OrderItemResponse.builder()
                                                .id(item.getId())
                                                .product(ProductResponse.builder()
                                                                .id(item.getProduct().getId())
                                                                .name(item.getProduct().getName())
                                                                .description(item.getProduct().getDescription())
                                                                .price(item.getProduct().getPrice())
                                                                .category(item.getProduct().getCategory() != null
                                                                                ? CategoryResponse.builder()
                                                                                                .id(item.getProduct()
                                                                                                                .getCategory()
                                                                                                                .getId())
                                                                                                .name(item.getProduct()
                                                                                                                .getCategory()
                                                                                                                .getName())
                                                                                                .description(item
                                                                                                                .getProduct()
                                                                                                                .getCategory()
                                                                                                                .getDescription())
                                                                                                .imageUrl(item.getProduct()
                                                                                                                .getCategory()
                                                                                                                .getImageUrl())
                                                                                                .createdAt(item.getProduct()
                                                                                                                .getCategory()
                                                                                                                .getCreatedAt())
                                                                                                .updatedAt(item.getProduct()
                                                                                                                .getCategory()
                                                                                                                .getUpdatedAt())
                                                                                                .build()
                                                                                : null)
                                                                .imageUrl(item.getProduct().getImageUrl())
                                                                .stockQuantity(item.getProduct().getStockQuantity())
                                                                .isActive(item.getProduct().getIsActive())
                                                                .build())
                                                .quantity(item.getQuantity())
                                                .price(item.getPrice())
                                                .build())
                                                .collect(Collectors.toList()))
                                .build();
        }
}
