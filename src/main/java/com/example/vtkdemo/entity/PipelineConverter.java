package com.example.vtkdemo.entity;

import com.example.vtkdemo.model.FilterDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import java.io.IOException;
import java.util.List;

public class PipelineConverter implements AttributeConverter<List<FilterDto>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<FilterDto> filters) {
        String json = null;
        try {
            json = objectMapper.writeValueAsString(filters);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }

    @Override
    public List<FilterDto> convertToEntityAttribute(String s) {
        List<FilterDto> filters = null;
        try {
            filters = objectMapper.readValue(s, List.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filters;
    }
}
