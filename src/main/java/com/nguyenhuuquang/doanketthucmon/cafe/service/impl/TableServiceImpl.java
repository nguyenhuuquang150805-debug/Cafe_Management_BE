package com.nguyenhuuquang.doanketthucmon.cafe.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nguyenhuuquang.doanketthucmon.cafe.dto.TableRequest;
import com.nguyenhuuquang.doanketthucmon.cafe.dto.TableResponse;
import com.nguyenhuuquang.doanketthucmon.cafe.entity.TableEntity;
import com.nguyenhuuquang.doanketthucmon.cafe.repository.TableRepository;
import com.nguyenhuuquang.doanketthucmon.cafe.service.TableService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TableServiceImpl implements TableService {

    private final TableRepository tableRepository;

    @Override
    public TableResponse createTable(TableRequest request) {
        TableEntity table = new TableEntity();
        table.setNumber(request.getNumber());
        table.setCapacity(request.getCapacity());
        table.setStatus(request.getStatus());
        table.setCreatedAt(LocalDateTime.now());
        table.setUpdatedAt(LocalDateTime.now());

        return mapToResponse(tableRepository.save(table));
    }

    @Override
    public List<TableResponse> getAllTables() {
        return tableRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TableResponse getTableById(Long id) {
        return tableRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Table not found"));
    }

    @Override
    public TableResponse updateTable(Long id, TableRequest request) {
        TableEntity table = tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Table not found"));

        table.setNumber(request.getNumber());
        table.setCapacity(request.getCapacity());
        table.setStatus(request.getStatus());
        table.setUpdatedAt(LocalDateTime.now());

        return mapToResponse(tableRepository.save(table));
    }

    @Override
    public void deleteTable(Long id) {
        TableEntity table = tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Table not found"));
        tableRepository.delete(table);
    }

    private TableResponse mapToResponse(TableEntity table) {
        return TableResponse.builder()
                .id(table.getId())
                .number(table.getNumber())
                .capacity(table.getCapacity())
                .status(table.getStatus())
                .createdAt(table.getCreatedAt())
                .updatedAt(table.getUpdatedAt())
                .build();
    }
}
