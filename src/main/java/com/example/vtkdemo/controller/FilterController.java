package com.example.vtkdemo.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.vtkdemo.entity.Filter;
import com.example.vtkdemo.exceptions.ResourceNotFoundException;
import com.example.vtkdemo.service.FilterService;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/filters", produces = {MediaType.APPLICATION_JSON_VALUE})
@Api(tags = {"Filter Controller"}, description = "Filter controller to query available filters")
public class FilterController {

    private final FilterResourceAssembler filterResourceAssembler;
    private final FilterService filterService;

    public FilterController(FilterResourceAssembler filterResourceAssembler, FilterService filterService) {
        this.filterResourceAssembler = filterResourceAssembler;
        this.filterService = filterService;
    }

    @Counted(value = "findAll.count")
    @Timed(value = "findAll.time")
    @GetMapping
    @Operation(summary = "Get all available filters with their methods")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of available filters list")
    })
    public CollectionModel<EntityModel<Filter>> findAll() {
        List<EntityModel<Filter>> filters = filterService.findAll().stream()
                .map(filterResourceAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(filters, linkTo(methodOn(this.getClass()).findAll()).withSelfRel());
    }

    @Counted(value = "findByName.count")
    @Timed(value = "findByName.time")
    @GetMapping("/{name}")
    @Operation(summary = "Get a filter by its class name or canonical name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of filter"),
            @ApiResponse(responseCode = "404", description = "Unsuccessful retrieval of filter")
    })
    public EntityModel<Filter> findByName(@PathVariable String name) {
        Filter filter = filterService.find(name)
                .orElseThrow(() -> new ResourceNotFoundException(name, Filter.class.getSimpleName(), "name"));

        return filterResourceAssembler.toModel(filter);
    }
}
