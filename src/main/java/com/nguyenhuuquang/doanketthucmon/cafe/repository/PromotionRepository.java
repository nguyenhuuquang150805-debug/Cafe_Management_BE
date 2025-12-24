package com.nguyenhuuquang.doanketthucmon.cafe.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nguyenhuuquang.doanketthucmon.cafe.entity.Promotion;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    @Query("SELECT p FROM Promotion p LEFT JOIN FETCH p.products WHERE p.id = :id")
    Optional<Promotion> findByIdWithProducts(@Param("id") Long id);

    @Query("SELECT DISTINCT p FROM Promotion p LEFT JOIN FETCH p.products")
    List<Promotion> findAllWithProducts();
}
