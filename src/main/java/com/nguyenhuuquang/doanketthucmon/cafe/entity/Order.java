package com.nguyenhuuquang.doanketthucmon.cafe.entity;

import com.nguyenhuuquang.doanketthucmon.cafe.entity.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "table_id", nullable = true)
    private TableEntity table;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private User employee;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;

    private BigDecimal totalAmount = BigDecimal.ZERO;

    @ManyToOne
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

    @Lob
    private String notes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items;
}
