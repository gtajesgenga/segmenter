package com.example.vtkdemo.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QueryRequestModel {

    @JsonProperty("StudyInstanceUID")
    public String StudyInstanceUID;

    @JsonProperty("SeriesInstanceUID")
    public String SeriesInstanceUID;
}
