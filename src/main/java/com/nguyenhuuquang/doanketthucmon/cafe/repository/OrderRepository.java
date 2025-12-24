package com.nguyenhuuquang.doanketthucmon.cafe.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nguyenhuuquang.doanketthucmon.cafe.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
