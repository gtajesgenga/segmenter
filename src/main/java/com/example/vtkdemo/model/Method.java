package com.example.vtkdemo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Method {

    private String name;
    private List<Parameter> parameters;

    @JsonIgnore
    public String getLabel() {
        return MessageFormat.format("{0}({1})",
                this.name,
                this.parameters.stream()
                        .map(parameter -> parameter.getDefaultCasting().getSimpleName())
                        .collect(Collectors.joining()));
    }
}
