package com.example.vtkdemo.entity;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder()
@NoArgsConstructor
@AllArgsConstructor
public class Filter {

    @Builder.Default
    private String uuid = UUID.randomUUID().toString();
    private String filterClass;
    private List<Method> methods;
}
