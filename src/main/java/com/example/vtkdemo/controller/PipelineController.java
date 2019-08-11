package com.example.vtkdemo.controller;

import com.example.vtkdemo.model.Filter;
import com.example.vtkdemo.model.Pipeline;
import com.example.vtkdemo.service.PipelineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

@Controller
@Slf4j
@RequestMapping(path = "/pipeline")
public class PipelineController {

    private final PipelineService pipelineService;

    @Autowired  public PipelineController(PipelineService pipelineService) {
        this.pipelineService = pipelineService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public ResponseEntity<byte[]> getVTK(@Validated @RequestBody Pipeline pipeline) {

        byte[] res = new byte[0];

        List<String> errors = new LinkedList<>();
        log.debug("Pipeline: " + pipeline.toString());

        try {
            res = pipelineService.execute(pipeline);
        } catch (InvocationTargetException e) {
            log.error("Error on invocation", e);
            errors.add(e.getCause().getMessage());
        }

        return ResponseEntity
                .ok()
                .header("Vtk-demo-errors", String.join("|", errors))
                .body(res);
    }

    @GetMapping(path = "/filter", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Filter>> getFilters() {
        return ResponseEntity.ok(PipelineService.getFilters());
    }
}
