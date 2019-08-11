package com.example.vtkdemo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "servers.orthanc")
@Getter
@Setter
public class OrthancServerConfig {

    public String host;

    public Integer port;

    public Map<String, String> endpoints;
}
