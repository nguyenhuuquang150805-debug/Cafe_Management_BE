package com.nguyenhuuquang.doanketthucmon.cafe.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nguyenhuuquang.doanketthucmon.cafe.dto.TableRequest;
import com.nguyenhuuquang.doanketthucmon.cafe.dto.TableResponse;
import com.nguyenhuuquang.doanketthucmon.cafe.service.TableService;

import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/tables")
@RequiredArgsConstructor
public class TableController {

    private final TableService tableService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<TableResponse> createTable(@RequestBody TableRequest request) {
        return ResponseEntity.ok(tableService.createTable(request));
    }

    @GetMapping
    public ResponseEntity<List<TableResponse>> getAllTables() {
        return ResponseEntity.ok(tableService.getAllTables());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TableResponse> getTableById(@PathVariable Long id) {
        return ResponseEntity.ok(tableService.getTableById(id));
    }

    @PreAuthorize("hasRole('STAFF') or hasRole('EMPLOYEE')")
    @PutMapping("/{id}")
    public ResponseEntity<TableResponse> updateTable(@PathVariable Long id, @RequestBody TableRequest request) {
        return ResponseEntity.ok(tableService.updateTable(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTable(@PathVariable Long id) {
        tableService.deleteTable(id);
        return ResponseEntity.noContent().build();
    }
}
