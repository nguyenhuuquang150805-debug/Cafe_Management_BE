package com.nguyenhuuquang.doanketthucmon.cafe.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "promotions")
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    private BigDecimal discountPercentage;
    private BigDecimal discountAmount;

    private LocalDate startDate;
    private LocalDate endDate;

    private Boolean isActive = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "promotion_products", joinColumns = @JoinColumn(name = "promotion_id"), inverseJoinColumns = @JoinColumn(name = "product_id"))
    @Builder.Default
    private Set<Product> products = new HashSet<>();

    // Helper đồng bộ 2 chiều
    public void addProduct(Product product) {
        this.products.add(product);
        product.getPromotions().add(this);
    }

    public void removeProduct(Product product) {
        this.products.remove(product);
        product.getPromotions().remove(this);
    }

    // Override equals và hashCode chỉ dựa trên ID
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Promotion))
            return false;
        Promotion that = (Promotion) o;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}