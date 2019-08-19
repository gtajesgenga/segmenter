package com.example.vtkdemo.controller;

import com.example.vtkdemo.model.FilterDto;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class FilterResourceAssembler implements ResourceAssembler<FilterDto, Resource<FilterDto>> {

    @Override
    public Resource<FilterDto> toResource(FilterDto filterDto) {
        String name = filterDto.getFilterClass().substring(filterDto.getFilterClass().lastIndexOf('.')+1);
        return new Resource<>(filterDto,
                linkTo(methodOn(FilterController.class).findByName(name)).withSelfRel(),
                linkTo(methodOn(FilterController.class).findAll()).withRel("filters"));
    }
}
