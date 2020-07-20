package com.example.vtkdemo.controller;

import com.example.vtkdemo.entity.PipelineEntity;
import org.springframework.data.rest.webmvc.ProfileResourceProcessor;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class PipelineResourceAssembler implements ResourceAssembler<PipelineEntity, Resource<PipelineEntity>> {
    @Override
    public Resource<PipelineEntity> toResource(PipelineEntity pipelineEntity) {
        return new Resource<>(pipelineEntity,
                linkTo(methodOn(PipelineController.class).findById(pipelineEntity.getId())).withSelfRel(),
                linkTo(methodOn(PipelineController.class).findAll()).withRel("pipelines"),
                new Link("http://localhost:8080/profile/pipelineEntities", ProfileResourceProcessor.PROFILE_REL)
        );
    }
}
