package com.example.vtkdemo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(description = "Model to represent a method internal-resource")
public class Method {

    @ApiModelProperty(accessMode = ApiModelProperty.AccessMode.READ_ONLY, example = "setInsideValue")
    private String name;
    @ApiModelProperty(value = "List of method's parameters", accessMode = ApiModelProperty.AccessMode.READ_WRITE)
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
