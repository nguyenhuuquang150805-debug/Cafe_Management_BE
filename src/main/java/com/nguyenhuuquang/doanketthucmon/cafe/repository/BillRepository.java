package com.nguyenhuuquang.doanketthucmon.cafe.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nguyenhuuquang.doanketthucmon.cafe.entity.Bill;

public interface BillRepository extends JpaRepository<Bill, Long> {
}
