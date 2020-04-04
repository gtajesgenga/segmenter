package com.example.vtkdemo.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder()
@NoArgsConstructor
@AllArgsConstructor
public class FilterDto {

    @Builder.Default
    private String uuid = UUID.randomUUID().toString();
    private String filterClass;
    private List<Method> methods;
}
