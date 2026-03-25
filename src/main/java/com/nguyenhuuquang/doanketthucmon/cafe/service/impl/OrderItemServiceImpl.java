package com.nguyenhuuquang.doanketthucmon.cafe.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nguyenhuuquang.doanketthucmon.cafe.dto.request.OrderItemRequest;
import com.nguyenhuuquang.doanketthucmon.cafe.dto.response.CategoryResponse;
import com.nguyenhuuquang.doanketthucmon.cafe.dto.response.OrderItemResponse;
import com.nguyenhuuquang.doanketthucmon.cafe.dto.response.OrderResponse;
import com.nguyenhuuquang.doanketthucmon.cafe.dto.response.ProductResponse;
import com.nguyenhuuquang.doanketthucmon.cafe.entity.Order;
import com.nguyenhuuquang.doanketthucmon.cafe.entity.OrderItem;
import com.nguyenhuuquang.doanketthucmon.cafe.entity.Product;
import com.nguyenhuuquang.doanketthucmon.cafe.repository.OrderItemRepository;
import com.nguyenhuuquang.doanketthucmon.cafe.repository.OrderRepository;
import com.nguyenhuuquang.doanketthucmon.cafe.repository.ProductRepository;
import com.nguyenhuuquang.doanketthucmon.cafe.service.OrderItemService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderItemServiceImpl implements OrderItemService {

        private final OrderItemRepository orderItemRepository;
        private final OrderRepository orderRepository;
        private final ProductRepository productRepository;

        // ✅ Inject SimpMessagingTemplate để broadcast WebSocket
        private final SimpMessagingTemplate messagingTemplate;

        @Override
        public OrderItemResponse createOrderItem(OrderItemRequest request) {
                OrderItem item = mapRequestToEntity(request, new OrderItem());
                orderItemRepository.save(item);

                OrderItemResponse response = mapToResponse(item);

                // ✅ Broadcast đến /topic/new-item-for-staff sau khi tạo item thành công
                // Đây là kênh Staff lắng nghe để nhận thông báo món mới từ khách
                messagingTemplate.convertAndSend("/topic/new-item-for-staff", response);
                System.out.println("📡 Broadcast món mới đến /topic/new-item-for-staff: "
                                + item.getProduct().getName());

                return response;
        }

        @Override
        public OrderItemResponse updateOrderItem(Long id, OrderItemRequest request) {
                OrderItem item = orderItemRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("OrderItem not found"));
                item = mapRequestToEntity(request, item);
                orderItemRepository.save(item);
                return mapToResponse(item);
        }

        @Override
        public OrderItemResponse getOrderItemById(Long id) {
                OrderItem item = orderItemRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("OrderItem not found"));
                return mapToResponse(item);
        }

        @Override
        public List<OrderItemResponse> getAllOrderItems() {
                return orderItemRepository.findAll().stream()
                                .map(this::mapToResponse)
                                .collect(Collectors.toList());
        }

        @Override
        public void deleteOrderItem(Long id) {
                OrderItem item = orderItemRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("OrderItem not found"));
                orderItemRepository.delete(item);
        }

        private OrderItem mapRequestToEntity(OrderItemRequest request, OrderItem item) {
                Order order = orderRepository.findById(request.getOrderId())
                                .orElseThrow(() -> new RuntimeException("Order not found"));
                Product product = productRepository.findById(request.getProductId())
                                .orElseThrow(() -> new RuntimeException("Product not found"));

                item.setOrder(order);
                item.setProduct(product);
                item.setQuantity(request.getQuantity());
                item.setPrice(request.getPrice());
                item.setSubtotal(request.getPrice().multiply(java.math.BigDecimal.valueOf(request.getQuantity())));

                if (item.getCreatedAt() == null) {
                        item.setCreatedAt(LocalDateTime.now());
                }
                item.setUpdatedAt(LocalDateTime.now());

                return item;
        }

        private OrderItemResponse mapToResponse(OrderItem item) {
                return OrderItemResponse.builder()
                                .id(item.getId())
                                .product(ProductResponse.builder()
                                                .id(item.getProduct().getId())
                                                .name(item.getProduct().getName())
                                                .description(item.getProduct().getDescription())
                                                .price(item.getProduct().getPrice())
                                                .category(item.getProduct().getCategory() != null
                                                                ? CategoryResponse.builder()
                                                                                .id(item.getProduct().getCategory()
                                                                                                .getId())
                                                                                .name(item.getProduct().getCategory()
                                                                                                .getName())
                                                                                .description(item.getProduct()
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
                                .order(item.getOrder() != null ? mapOrderToResponse(item.getOrder()) : null)
                                .quantity(item.getQuantity())
                                .price(item.getPrice())
                                .subtotal(item.getSubtotal())
                                .createdAt(item.getCreatedAt())
                                .updatedAt(item.getUpdatedAt())
                                .build();
        }

        private OrderResponse mapOrderToResponse(Order order) {
                return OrderResponse.builder()
                                .id(order.getId())
                                .table(order.getTable() != null
                                                ? com.nguyenhuuquang.doanketthucmon.cafe.dto.response.TableResponse
                                                                .builder()
                                                                .id(order.getTable().getId())
                                                                .number(order.getTable().getNumber())
                                                                .capacity(order.getTable().getCapacity())
                                                                .status(order.getTable().getStatus())
                                                                .createdAt(order.getTable().getCreatedAt())
                                                                .updatedAt(order.getTable().getUpdatedAt())
                                                                .build()
                                                : null)
                                .employee(order.getEmployee() != null
                                                ? com.nguyenhuuquang.doanketthucmon.cafe.dto.response.UserResponse
                                                                .builder()
                                                                .id(order.getEmployee().getId())
                                                                .username(order.getEmployee().getUsername())
                                                                .fullName(order.getEmployee().getFullName())
                                                                .email(order.getEmployee().getEmail())
                                                                .build()
                                                : null)
                                .status(order.getStatus() != null ? order.getStatus().name() : null)
                                .totalAmount(order.getTotalAmount())
                                .promotion(order.getPromotion() != null
                                                ? com.nguyenhuuquang.doanketthucmon.cafe.dto.response.PromotionResponse
                                                                .builder()
                                                                .id(order.getPromotion().getId())
                                                                .name(order.getPromotion().getName())
                                                                .discountAmount(order.getPromotion()
                                                                                .getDiscountAmount())
                                                                .discountPercentage(order.getPromotion()
                                                                                .getDiscountPercentage())
                                                                .startDate(order.getPromotion().getStartDate())
                                                                .endDate(order.getPromotion().getEndDate())
                                                                .isActive(order.getPromotion().getIsActive())
                                                                .createdAt(order.getPromotion().getCreatedAt())
                                                                .updatedAt(order.getPromotion().getUpdatedAt())
                                                                .build()
                                                : null)
                                .notes(order.getNotes())
                                .createdAt(order.getCreatedAt())
                                .updatedAt(order.getUpdatedAt())
                                .items(null)
                                .build();
        }
}