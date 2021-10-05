package com.example.vtkdemo.controller;

import com.example.vtkdemo.config.ApplicationConfig;
import com.example.vtkdemo.service.VtkService;
import com.sun.istack.NotNull;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/api/vtk")
@Api(tags = {"VTK Controller"}, description = "VTK controller to generate segmentations")
public class VtkController {

    private final VtkService vtkService;

    private final ApplicationConfig applicationConfig;

    public VtkController(VtkService vtkService, ApplicationConfig applicationConfig) {
        this.vtkService = vtkService;
        this.applicationConfig = applicationConfig;
    }

    @GetMapping(value = "/study/{studyId}/serie/{serieId}/pipeline/{pipelineId}", produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    @Operation(summary = "Get a vtk file with segmented volume")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of segmentation")
    })
    @Counted(value = "getVTK.count")
    @Timed(value = "getVTK.time")
    @ResponseBody
    public ResponseEntity<byte[]> getVTK(@NotNull @PathVariable String studyId,
                                         @NotNull @PathVariable String serieId,
                                         @NotNull @PathVariable Long pipelineId) {

        byte[] res = new byte[0];

        List<String> errors = new LinkedList<>();

        try {
            res = vtkService.execute(studyId, serieId, pipelineId);
            FileUtils.cleanDirectory(Paths.get(applicationConfig.getTempFolder()).toFile());
        } catch (InvocationTargetException e) {
            log.error("Error on invocation", e);
            errors.add(e.getCause().getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ResponseEntity
                .ok()
                .header("Vtk-demo-errors", String.join("|", errors))
                .body(res);
    }

}
