package com.example.vtkdemo.controller;

import com.example.vtkdemo.service.VtkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/vtk")
public class VtkController {

    private final VtkService vtkService;

    public VtkController(VtkService vtkService) {
        this.vtkService = vtkService;
    }

    @GetMapping(value = "/study/{studyId}/serie/{serieId}/pipeline/{pipelineId}", produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    @ResponseBody
    public ResponseEntity<byte[]> getVTK(@NotNull @PathVariable String studyId,
                                         @NotNull @PathVariable String serieId,
                                         @NotNull @PathVariable Long pipelineId) {

        byte[] res = new byte[0];

        List<String> errors = new LinkedList<>();

        try {
            res = vtkService.execute(studyId, serieId, pipelineId);
        } catch (InvocationTargetException e) {
            log.error("Error on invocation", e);
            errors.add(e.getCause().getMessage());
        }

        return ResponseEntity
                .ok()
                .header("Vtk-demo-errors", String.join("|", errors))
                .body(res);
    }

}
