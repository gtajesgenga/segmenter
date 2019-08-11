package com.example.vtkdemo.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FindRequestModel {

    @JsonProperty("Level")
    public String Level;
    @JsonProperty("Query")
    public QueryRequestModel Query;
}
