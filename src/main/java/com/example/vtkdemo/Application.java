package com.example.vtkdemo;

import com.example.vtkdemo.config.ApplicationConfig;
import com.example.vtkdemo.config.OrthancServerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication()
@EnableConfigurationProperties({ApplicationConfig.class, OrthancServerConfig.class})
@ComponentScan("com.example.vtkdemo")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
