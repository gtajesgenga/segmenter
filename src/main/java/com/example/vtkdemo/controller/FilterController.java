package com.example.vtkdemo.controller;

import com.example.vtkdemo.entity.Filter;
import com.example.vtkdemo.exceptions.ResourceNotFoundException;
import com.example.vtkdemo.service.FilterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "/api/filters", produces = {MediaType.APPLICATION_JSON_VALUE})
public class FilterController {

    private final FilterResourceAssembler filterResourceAssembler;

    public FilterController(FilterResourceAssembler filterResourceAssembler) {
        this.filterResourceAssembler = filterResourceAssembler;
    }

    @GetMapping
    @Operation(summary = "Get all available filters with their methods")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of available filters list")
    })
    public CollectionModel<EntityModel<Filter>> findAll() {
        List<EntityModel<Filter>> filters = FilterService.findAll().stream()
                .map(filterResourceAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(filters, linkTo(methodOn(this.getClass()).findAll()).withSelfRel());
    }

    @GetMapping("/{name}")
    @Operation(summary = "Get a filters by its class name or canonical name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of filter"),
            @ApiResponse(responseCode = "404", description = "Unsuccessful retrieval of filter")
    })
    public EntityModel<Filter> findByName(@PathVariable String name) {
        Filter filter = FilterService.find(name)
                .orElseThrow(() -> new ResourceNotFoundException(name, Filter.class.getSimpleName(), "name"));

        return filterResourceAssembler.toModel(filter);
    }
}
