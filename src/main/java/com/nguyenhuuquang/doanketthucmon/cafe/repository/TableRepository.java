package com.nguyenhuuquang.doanketthucmon.cafe.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nguyenhuuquang.doanketthucmon.cafe.entity.TableEntity;

public interface TableRepository extends JpaRepository<TableEntity, Long> {
}
