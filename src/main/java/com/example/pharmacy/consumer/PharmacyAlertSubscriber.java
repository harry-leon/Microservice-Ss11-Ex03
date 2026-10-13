package com.example.pharmacy.consumer;

import com.example.pharmacy.dto.AlertMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class PharmacyAlertSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String body = new String(message.getBody(), StandardCharsets.UTF_8);
            AlertMessage alert = objectMapper.readValue(body, AlertMessage.class);

            log.info("=== THÔNG BÁO DASHBOARD QUẢN LÝ ===");
            log.info("Type : {}", alert.getType());
            log.info("Nội dung: {}", alert.getMessage());
            log.info("=================================");
        } catch (Exception e) {
            log.error("Lỗi khi xử lý message từ Redis channel: {}", e.getMessage(), e);
        }
    }
}
