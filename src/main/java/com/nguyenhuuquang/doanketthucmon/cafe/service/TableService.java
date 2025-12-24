package com.nguyenhuuquang.doanketthucmon.cafe.service;

import java.util.List;

import com.nguyenhuuquang.doanketthucmon.cafe.dto.TableRequest;
import com.nguyenhuuquang.doanketthucmon.cafe.dto.TableResponse;

public interface TableService {
    TableResponse createTable(TableRequest request);

    List<TableResponse> getAllTables();

    TableResponse getTableById(Long id);

    TableResponse updateTable(Long id, TableRequest request);

    void deleteTable(Long id);
}
