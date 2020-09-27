package com.example.vtkdemo.entity;

import java.util.List;
import java.util.UUID;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder()
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Model to represent a filter resource")
public class Filter implements Comparable<Filter> {

    @Builder.Default
    @ApiModelProperty(value = "Unique identifier", accessMode = ApiModelProperty.AccessMode.READ_ONLY, example = "80b08c91-4222-49b7-82be-01d453b1acfe")
    private String uuid = UUID.randomUUID().toString();
    @ApiModelProperty(value = "Canonical name of the class that implements the filter", accessMode = ApiModelProperty.AccessMode.READ_WRITE, example = "org.itk.simple.BinaryThresholdImageFilter")
    private String filterClass;
    @ApiModelProperty(value = "List of methods available for the filter", accessMode = ApiModelProperty.AccessMode.READ_WRITE)
    private List<Method> methods;

    @Override
    public int compareTo(Filter o) {
        return this.filterClass.compareTo(o.filterClass);
    }
}
