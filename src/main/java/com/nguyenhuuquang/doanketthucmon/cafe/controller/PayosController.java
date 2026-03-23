package com.nguyenhuuquang.doanketthucmon.cafe.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nguyenhuuquang.doanketthucmon.cafe.service.OrderService;
import com.nguyenhuuquang.doanketthucmon.cafe.service.PayosService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PayosController {

    private final PayosService payosService;
    private final OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<?> createPayment(@RequestBody CreatePaymentDto dto) {
        try {
            System.out.println("📥 Nhận request: orderId=" + dto.orderId + ", amount=" + dto.amount);

            Long orderCode = System.currentTimeMillis() / 1000;

            Map<String, Object> resp = payosService.createPaymentLink(
                    orderCode,
                    dto.amount,
                    dto.description != null ? dto.description : "Thanh toán đơn hàng #" + dto.orderId,
                    dto.returnUrl != null ? dto.returnUrl : "http://localhost:3000/staff/payos-return",
                    dto.cancelUrl != null ? dto.cancelUrl : "http://localhost:3000/staff/payos-return",
                    dto.expiredAt);

            System.out.println("✅ PayOS response: " + resp);

            Map<String, Object> responseWithOrderId = new HashMap<>();
            responseWithOrderId.put("code", resp.get("code"));
            responseWithOrderId.put("desc", resp.get("desc"));
            responseWithOrderId.put("data", resp.get("data"));
            responseWithOrderId.put("signature", resp.get("signature"));
            responseWithOrderId.put("orderId", dto.orderId);
            responseWithOrderId.put("orderCode", orderCode);

            return ResponseEntity.ok(responseWithOrderId);

        } catch (Exception e) {
            System.err.println("❌ Lỗi: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/mapping/{orderCode}")
    public ResponseEntity<?> getOrderIdByOrderCode(@PathVariable Long orderCode) {
        try {

            return ResponseEntity.status(501).body(Map.of(
                    "success", false,
                    "message", "Chức năng mapping chưa được implement. Vui lòng truyền orderId qua URL param"));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<?> handleWebhook(@RequestBody Map<String, Object> webhookData) {
        try {
            System.out.println("🔔 Nhận webhook từ PayOS: " + webhookData);

            String code = (String) webhookData.get("code");
            Long orderCode = Long.parseLong(webhookData.get("orderCode").toString());
            String status = (String) webhookData.get("status");

            System.out.println(
                    "📋 Webhook details - code: " + code + ", orderCode: " + orderCode + ", status: " + status);

            if ("00".equals(code) || "PAID".equals(status)) {
                System.out.println("✅ Thanh toán thành công - cần cập nhật Order");

            }

            return ResponseEntity.ok(Map.of("success", true));

        } catch (Exception e) {
            System.err.println("❌ Lỗi xử lý webhook: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/status/{orderCode}")
    public ResponseEntity<?> getPaymentStatus(@PathVariable Long orderCode) {
        try {
            System.out.println("🔍 Kiểm tra trạng thái thanh toán: " + orderCode);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Lấy trạng thái thành công"));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @PostMapping("/cancel/{orderCode}")
    public ResponseEntity<?> cancelPayment(@PathVariable Long orderCode) {
        try {
            System.out.println("🚫 Hủy thanh toán: " + orderCode);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Đã hủy thanh toán"));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    public static class CreatePaymentDto {
        public Long orderId;
        public Long amount;
        public String productName;
        public Integer quantity;
        public Long price;
        public String description;
        public String returnUrl;
        public String cancelUrl;
        public Integer expiredAt;
    }
}