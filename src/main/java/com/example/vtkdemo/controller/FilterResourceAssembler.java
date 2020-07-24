package com.example.vtkdemo.controller;

import com.example.vtkdemo.model.FilterDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class FilterResourceAssembler implements RepresentationModelAssembler<FilterDto, EntityModel<FilterDto>> {

    @Override
    public EntityModel<FilterDto> toModel(FilterDto filterDto) {
        String name = filterDto.getFilterClass().substring(filterDto.getFilterClass().lastIndexOf('.')+1);
        return new EntityModel<>(filterDto,
                linkTo(methodOn(FilterController.class).findByName(name)).withSelfRel(),
                linkTo(methodOn(FilterController.class).findAll()).withRel("filters"));
    }
}
