package com.example.pharmacy.publisher;

import com.example.pharmacy.dto.AlertMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PharmacyAlertPublisher {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    public void publishAlert(String channel, AlertMessage alertMessage) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(alertMessage);
            stringRedisTemplate.convertAndSend(channel, jsonPayload);
            log.info("Đã gửi thông báo đến kênh : {}", channel);
        } catch (JsonProcessingException e) {
            log.error("Lỗi parse đối tượng JSON khi gửi alert: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi chuyển đổi thông điệp JSON", e);
        }
    }
}
