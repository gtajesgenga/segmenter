package com.example.vtkdemo.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class PipelineRequest {

    @NonNull
    private String name;

    @NonNull
    private PipelineDto pipelineDto;
}
