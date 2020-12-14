package com.example.vtkdemo;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.vtkdemo.controller.VtkController;
import com.example.vtkdemo.entity.Filter;
import com.example.vtkdemo.entity.Pipeline;
import com.example.vtkdemo.repository.PipelineRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

@ExtendWith(SpringExtension.class)
//@ActiveProfiles("heroku")
@SpringBootTest(classes = Application.class)
public class PipelineDtoEntityControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    PipelineRepository repository;
    @Autowired
    VtkController vtkController;
    private Long id;

    @BeforeEach
    public void setUp() throws IOException {
        InputStream inputStream = new ClassPathResource("filters.json").getInputStream();
        List<Filter> filters;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream)) ) {
            filters = objectMapper.readValue(reader.lines()
                    .collect(Collectors.joining("\n")), List.class);
        }

        Pipeline request = Pipeline.builder()
                .name("pipeline-test")
                .filters(filters)
                .build();

        Pipeline response = repository.save(request);
        id = response.getId();
    }

    @AfterEach
    public void setDown() {
        repository.deleteById(id);
    }

    @Test
    @Disabled
    public void testPostMethod() {
        ResponseEntity<byte[]> result = vtkController.getVTK("1.2.276.0.7230010.3.1.2.8323329.22968.1583329708.26063",
                "1.2.276.0.7230010.3.1.3.8323329.22968.1583329709.26297",
                id);
        try {
            assertThat(IOUtils.toByteArray(this.getClass().getResourceAsStream("/test.vtk")), equalTo(result.getBody()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
