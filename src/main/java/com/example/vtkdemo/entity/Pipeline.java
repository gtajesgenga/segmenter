package com.example.vtkdemo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "pipeline")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pipeline {

    private @Version @JsonIgnore
    Long version;

    @Id @Column(name = "id") @GeneratedValue
    private Long id;

    @Column(name = "name", columnDefinition = "varchar(128)", length = 128, nullable = false)
    private String name;

    @EqualsAndHashCode.Exclude
    @Column(name = "json", columnDefinition = "text", nullable = false)
    @Convert(converter = PipelineConverter.class)
    private List<Filter> filters;
}
