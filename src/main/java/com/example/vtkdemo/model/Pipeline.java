package com.example.vtkdemo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pipeline {

    @NotNull
    private String inputPath;

    @NotNull
    private String outputFile;

    private List<Filter> filters;
}
