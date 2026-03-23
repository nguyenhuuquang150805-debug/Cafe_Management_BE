package com.nguyenhuuquang.doanketthucmon.cafe.service;

import java.util.List;

import com.nguyenhuuquang.doanketthucmon.cafe.dto.request.BillRequest;
import com.nguyenhuuquang.doanketthucmon.cafe.dto.response.BillResponse;

public interface BillService {
    BillResponse createBill(BillRequest request);

    BillResponse updateBill(Long id, BillRequest request);

    BillResponse getBillById(Long id);

    List<BillResponse> getAllBills();

    void deleteBill(Long id);
}
