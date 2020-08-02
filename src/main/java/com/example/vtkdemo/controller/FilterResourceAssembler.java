package com.example.vtkdemo.controller;

import com.example.vtkdemo.entity.Filter;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class FilterResourceAssembler implements RepresentationModelAssembler<Filter, EntityModel<Filter>> {

    @Override
    public EntityModel<Filter> toModel(Filter filter) {
        String name = filter.getFilterClass().substring(filter.getFilterClass().lastIndexOf('.') + 1);
        return EntityModel.of(filter,
                linkTo(methodOn(FilterController.class).findByName(name)).withSelfRel(),
                linkTo(methodOn(FilterController.class).findAll()).withRel("filters"));
    }
}
