package com.grepp.diary.app.model.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmtpDto {

    private String from;
    private String subject;
    private List<String> to;
    private Map<String, String> properties = new HashMap<>();
    private String eventType;
}