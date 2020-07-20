package com.example.vtkdemo.controller;

import com.example.vtkdemo.exceptions.ResourceNotFoundException;
import com.example.vtkdemo.model.FilterDto;
import com.example.vtkdemo.service.FilterService;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "/api/filters", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class FilterController {

    private final FilterResourceAssembler filterResourceAssembler;

    public FilterController(FilterResourceAssembler filterResourceAssembler) {
        this.filterResourceAssembler = filterResourceAssembler;
    }

    @GetMapping
    public Resources<Resource<FilterDto>> findAll() {
        List<Resource<FilterDto>> filters = FilterService.findAll().stream()
                .map(filterResourceAssembler::toResource)
                .collect(Collectors.toList());

        return new Resources<>(filters, linkTo(methodOn(this.getClass()).findAll()).withSelfRel());
    }

    @GetMapping("/{name}")
    public Resource<FilterDto> findByName(@PathVariable String name) {
        FilterDto filterDto = FilterService.find(name)
                .orElseThrow(() -> new ResourceNotFoundException(name, FilterDto.class.getSimpleName(), "name"));

        return filterResourceAssembler.toResource(filterDto);
    }
}
