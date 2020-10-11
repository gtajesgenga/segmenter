package com.example.vtkdemo.config;

import com.example.vtkdemo.client.LoggingRequestInterceptor;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.Collections;

@ConfigurationProperties(prefix = "application")
@Getter
@Setter
@Slf4j
public class ApplicationConfig {

    String tempFolder;

    Boolean restTemplateLogin;

    @PostConstruct
    private void initialize() {
        try {
            File directory = new File(tempFolder);
            if (!directory.exists() && !directory.mkdir()) {
                    log.warn("eventType=error message=Error on creation of temp directory");
            }
            FileUtils.cleanDirectory(directory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Bean(name = "restTemplate")
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        RestTemplate restTemplate = builder
                .build();
        if (restTemplateLogin) {
            restTemplate.setInterceptors(Collections.singletonList(new LoggingRequestInterceptor()));
        }
        return restTemplate;
    }
}
