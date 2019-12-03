package com.example.vtkdemo;

import com.example.vtkdemo.controller.PipelineController;
import com.example.vtkdemo.controller.VtkController;
import com.example.vtkdemo.entity.PipelineEntity;
import com.example.vtkdemo.model.FilterDto;
import com.example.vtkdemo.model.PipelineDto;
import com.example.vtkdemo.model.PipelineRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
//@ActiveProfiles("heroku")
@SpringBootTest(classes = Application.class)
public class PipelineDtoEntityControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    PipelineController pipelineController;
    @Autowired
    VtkController vtkController;
    private Long id;

    @Before
    public void setUp() throws IOException {
        InputStream inputStream = new ClassPathResource("filters.json").getInputStream();
        List<FilterDto> filters;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream)) ) {
            filters = objectMapper.readValue(reader.lines()
                    .collect(Collectors.joining("\n")), List.class);
        }

        PipelineRequest request = new PipelineRequest("pipeline-test", PipelineDto.builder()
                .filters(filters)
                .build());

        Resource<PipelineEntity> response = pipelineController.createPipeline(request);
        id = response.getContent().getId();
    }

    @After
    public void setDown() {
        pipelineController.deletePipeline(id);
    }

    @Test
    public void testPostMethod() {
        ResponseEntity<byte[]> result = vtkController.getVTK("1.2.840.113619.2.55.1.1762927707.1872.1482485885.841",
                "1.2.840.113619.2.80.975520672.24749.1482491986.1.4.1",
                id);
        try {
            assertThat(IOUtils.toByteArray(this.getClass().getResourceAsStream("/test.vtk")), equalTo(result.getBody()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
