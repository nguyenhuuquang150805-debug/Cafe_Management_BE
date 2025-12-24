package com.nguyenhuuquang.doanketthucmon.cafe.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nguyenhuuquang.doanketthucmon.cafe.dto.BillRequest;
import com.nguyenhuuquang.doanketthucmon.cafe.dto.BillResponse;
import com.nguyenhuuquang.doanketthucmon.cafe.service.BillService;

import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/bills")
@RequiredArgsConstructor
public class BillController {

    private final BillService billService;

    @PostMapping
    public ResponseEntity<BillResponse> createBill(@RequestBody BillRequest request) {
        BillResponse response = billService.createBill(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BillResponse> updateBill(@PathVariable Long id,
            @RequestBody BillRequest request) {
        BillResponse response = billService.updateBill(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<BillResponse>> getAllBills() {
        List<BillResponse> responseList = billService.getAllBills();
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BillResponse> getBillById(@PathVariable Long id) {
        BillResponse response = billService.getBillById(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBill(@PathVariable Long id) {
        billService.deleteBill(id);
        return ResponseEntity.noContent().build();
    }
}
