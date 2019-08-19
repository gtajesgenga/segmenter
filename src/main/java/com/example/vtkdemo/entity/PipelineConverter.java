package com.example.vtkdemo.entity;

import com.example.vtkdemo.model.PipelineDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import java.io.IOException;

public class PipelineConverter implements AttributeConverter<PipelineDto, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(PipelineDto pipelineDto) {
        String json = null;
        try {
            json = objectMapper.writeValueAsString(pipelineDto);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }

    @Override
    public PipelineDto convertToEntityAttribute(String s) {
        PipelineDto pipelineDto = null;
        try {
            pipelineDto = objectMapper.readValue(s, PipelineDto.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pipelineDto;
    }
}
