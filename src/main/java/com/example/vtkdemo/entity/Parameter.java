package com.example.vtkdemo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Map;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Builder
@NoArgsConstructor
@ApiModel(description = "Model to represent a parameter internal-resource")
public class Parameter {

    @ApiModelProperty(accessMode = ApiModelProperty.AccessMode.READ_ONLY, example = "arg0")
    private String name;

    @Setter(AccessLevel.NONE)
    @JsonIgnore
    @Builder.Default
    @ApiModelProperty(value = "Default class casting for parameter value", accessMode = ApiModelProperty.AccessMode.READ_ONLY, example = "java.lang.Short")
    private Class<?> defaultCasting = Number.class;

    @Setter(AccessLevel.NONE)
    @JsonIgnore
    @Builder.Default
    @ApiModelProperty(value = "Class casting for parameter value when the parameter is multidimensional", accessMode = ApiModelProperty.AccessMode.READ_ONLY, example = "java.lang.Long")
    private Class<?> multidimensionalClass = null;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Builder.Default
    @ApiModelProperty(value = "Multidimensional class name for parameter value when the parameter is multidimensional", accessMode = ApiModelProperty.AccessMode.READ_WRITE, example = "java.lang.Long")
    private String multidimensional = null;

    @ApiModelProperty(value = "Class name of parameter value", accessMode = ApiModelProperty.AccessMode.READ_WRITE, example = "java.lang.Short")
    private String casting;

    @ApiModelProperty(value = "Parameter value", accessMode = ApiModelProperty.AccessMode.READ_WRITE, example = "255")
    private String value;

    @Builder.Default
    private Boolean hasValues = false;

    private Map<String, Integer> values;

    private Parameter(String name, Class<?> defaultCasting, Class<?> multidimensionalClass, String multidimensional, String casting, String value, Boolean hasValues, Map<String, Integer> values) {
        this.name = name;
        this.defaultCasting = defaultCasting;
        this.multidimensionalClass = multidimensionalClass;
        this.setMultidimensional(multidimensional);
        this.setCasting(casting);
        this.value = value;
        this.hasValues = hasValues;
        this.values = values;
    }

    private void setCasting(String casting) {
        this.casting = casting;
        try {
            this.defaultCasting = Class.forName(this.casting);
        } catch (ClassNotFoundException e) {
            try {
                int lastIndex = this.casting.lastIndexOf('.');
                String prefix = this.casting.substring(0, lastIndex);
                String suffix = this.casting.substring(lastIndex + 1);
                String clazz = prefix + "$" + suffix;
                this.defaultCasting = Class.forName(clazz);
            } catch (ClassNotFoundException ex) {
                log.error("Casting class doesn't exists.", e);
            }
        }
    }

    private void setMultidimensional(String multidimensional) {
        this.multidimensional = multidimensional;

        if (this.multidimensional != null) {
            try {
                this.multidimensionalClass = Class.forName(this.multidimensional);
            } catch (ClassNotFoundException e) {
                log.error("Casting class doesn't exists.", e);
            }
        }
    }

}
