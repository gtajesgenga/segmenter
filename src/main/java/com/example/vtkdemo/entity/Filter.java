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
public class Filter implements Comparable<Filter> {

    @Builder.Default
    private String uuid = UUID.randomUUID().toString();
    private String filterClass;
    private List<Method> methods;

    @Override
    public int compareTo(Filter o) {
        return this.filterClass.compareTo(o.filterClass);
    }
}
