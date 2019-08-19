package com.example.vtkdemo.controller;

import com.example.vtkdemo.entity.PipelineEntity;
import com.example.vtkdemo.exceptions.ResourceNotFoundException;
import com.example.vtkdemo.model.PipelineRequest;
import com.example.vtkdemo.service.PipelineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@Slf4j
@RequestMapping(path = "/pipelines", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class PipelineController {

    private final PipelineService pipelineService;
    private final PipelineResourceAssembler pipelineResourceAssembler;

    public PipelineController(PipelineService pipelineService, PipelineResourceAssembler pipelineResourceAssembler) {
        this.pipelineService = pipelineService;
        this.pipelineResourceAssembler = pipelineResourceAssembler;
    }

    @GetMapping("/{id}")
    public Resource<PipelineEntity> findById(@PathVariable Long id) {
        log.info("PipelineEntity get request for id={}", id);
        PipelineEntity pipelineEntity = pipelineService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id.toString(), PipelineEntity.class.getSimpleName(), "id"));

        return pipelineResourceAssembler.toResource(pipelineEntity);
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public Resource<PipelineEntity> createPipeline(@RequestBody PipelineRequest pipeline) {
        log.info("PipelineEntity creation request for: {}", pipeline);
        return pipelineResourceAssembler.toResource(pipelineService.createPipeline(pipeline));
    }

    @PutMapping("/{id}")
    public Resource<PipelineEntity> updateById(@PathVariable Long id, @RequestBody PipelineRequest pipeline) {
        log.info("PipelineEntity update request for id={} {}", id, pipeline);
        PipelineEntity _new = pipelineService.updateById(id, pipeline)
                .orElseThrow(() -> new ResourceNotFoundException(id.toString(), PipelineEntity.class.getSimpleName(), "id"));

        return pipelineResourceAssembler.toResource(_new);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deletePipeline(@PathVariable Long id) {
        log.info("PipelineEntity delete request for id={}", id);
        pipelineService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public Resources<Resource<PipelineEntity>> findAll() {
        log.info("Pipelines collection request");
        List<Resource<PipelineEntity>> pipelines = pipelineService.findAll().stream()
                .map(pipelineResourceAssembler::toResource)
                .collect(Collectors.toList());

        return new Resources<>(pipelines,
                linkTo(methodOn(this.getClass()).findAll()).withSelfRel());
    }
}
