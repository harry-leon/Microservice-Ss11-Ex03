package com.example.pharmacy.controller;

import com.example.pharmacy.dto.AlertMessage;
import com.example.pharmacy.publisher.PharmacyAlertPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/alerts")
@RequiredArgsConstructor
public class PharmacyAlertController {

    private final PharmacyAlertPublisher publisher;

    @Value("${app.redis.channel.pharmacy-alerts:pharmacy-alerts}")
    private String defaultChannel;

    @PostMapping
    public ResponseEntity<String> sendAlert(@RequestBody AlertMessage request) {
        String channel = (request.getTopicListen() != null && !request.getTopicListen().isBlank())
                ? request.getTopicListen()
                : defaultChannel;

        publisher.publishAlert(channel, request);

        return ResponseEntity.ok("Đã gửi thông báo đến kênh : " + channel);
    }
}
