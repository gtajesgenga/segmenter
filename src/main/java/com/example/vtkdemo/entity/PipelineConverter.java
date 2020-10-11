package com.example.vtkdemo.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import java.io.IOException;
import java.util.List;

class PipelineConverter implements AttributeConverter<List<Filter>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Filter> filters) {
        String json = null;
        try {
            json = objectMapper.writeValueAsString(filters);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }

    @Override
    public List<Filter> convertToEntityAttribute(String s) {
        List<Filter> filters = null;
        try {
            filters = objectMapper.readValue(s, List.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filters;
    }
}
