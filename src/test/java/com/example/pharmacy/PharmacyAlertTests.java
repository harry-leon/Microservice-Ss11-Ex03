package com.example.pharmacy;

import com.example.pharmacy.consumer.PharmacyAlertSubscriber;
import com.example.pharmacy.dto.AlertMessage;
import com.example.pharmacy.publisher.PharmacyAlertPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.DefaultMessage;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class PharmacyAlertTests {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testSubscriberProcessesMessageSuccessfully() throws Exception {
        PharmacyAlertSubscriber subscriber = new PharmacyAlertSubscriber(objectMapper);

        AlertMessage alert = AlertMessage.builder()
                .type("IMPORT")
                .message("Đã nhập 100 hộp Panadol")
                .topicListen("pharmacy-alerts")
                .build();

        String json = objectMapper.writeValueAsString(alert);
        Message message = new DefaultMessage("pharmacy-alerts".getBytes(StandardCharsets.UTF_8), json.getBytes(StandardCharsets.UTF_8));

        assertDoesNotThrow(() -> subscriber.onMessage(message, null));
    }

    @Test
    void testPublisherSendsMessage() throws Exception {
        StringRedisTemplate redisTemplate = mock(StringRedisTemplate.class);
        PharmacyAlertPublisher publisher = new PharmacyAlertPublisher(redisTemplate, objectMapper);

        AlertMessage alert = AlertMessage.builder()
                .type("IMPORT")
                .message("Đã nhập 100 hộp Panadol")
                .topicListen("pharmacy-alerts")
                .build();

        publisher.publishAlert("pharmacy-alerts", alert);

        String expectedJson = objectMapper.writeValueAsString(alert);
        verify(redisTemplate, times(1)).convertAndSend("pharmacy-alerts", expectedJson);
    }
}
