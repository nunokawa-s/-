package com.example.NAGOYAMESHI.entity;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class OpeningHoursConverter implements AttributeConverter<Map<String, String>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, String> attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            // ログ出力や例外処理を適切に行う
            return null;
        }
    }

    @Override
    public Map<String, String> convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            return objectMapper.readValue(dbData, new TypeReference<Map<String, String>>() {});
        } catch (IOException e) {
            // ログ出力や例外処理を適切に行う
            return null;
        }
    }
}