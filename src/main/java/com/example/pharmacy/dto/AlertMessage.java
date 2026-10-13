package com.example.pharmacy.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertMessage implements Serializable {

    @JsonProperty("type")
    @JsonAlias({"type", "typeAlert", "Type"})
    private String type;

    @JsonProperty("message")
    @JsonAlias({"message", "msg", "content", "noiDung"})
    private String message;

    @JsonProperty("topicListen")
    private String topicListen;
}
