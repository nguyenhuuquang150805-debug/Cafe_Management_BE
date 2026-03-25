package com.nguyenhuuquang.doanketthucmon.cafe.controller;

import java.util.Map;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class OrderSocketController {

    @MessageMapping("/add-order-item")
    @SendTo("/topic/new-item-for-staff")
    public Map<String, Object> handleNewOrderItem(Map<String, Object> itemData) {
        System.out.println("📡 WebSocket: Món mới được thêm: " + itemData);
        return itemData;
    }

}