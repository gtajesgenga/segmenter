package com.example.vtkdemo.model;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class PipelineRequest {

    @NonNull
    private String name;

    @NonNull
    private PipelineDto pipelineDto;
}
