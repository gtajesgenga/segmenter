package com.example.vtkdemo.controller;

import com.example.vtkdemo.model.Filter;
import com.example.vtkdemo.model.Pipeline;
import com.example.vtkdemo.service.PipelineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<byte[]> getVTK(@RequestBody Pipeline pipeline) {
        log.debug("Pipeline: " + pipeline.toString());

        pipelineService.execute(pipeline);

        return ResponseEntity.ok(new byte[]{});
    }

    @GetMapping(path = "/filter", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Filter>> getFilters() {
        return ResponseEntity.ok(pipelineService.getFilters());
    }
}
