package com.example.vtkdemo.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.vtkdemo.client.model.FindRequestModel;
import com.example.vtkdemo.client.model.QueryRequestModel;
import com.example.vtkdemo.config.OrthancServerConfig;
import com.example.vtkdemo.logging.EnableOutgoingLogging;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OrthancClient {

    private static final String INSTANCE = "Instance";
    private final OrthancServerConfig orthancServerConfig;

    private final RestTemplate restTemplate;


    @Autowired public OrthancClient(OrthancServerConfig orthancServerConfig, RestTemplate restTemplate) {
        this.orthancServerConfig = orthancServerConfig;
        this.restTemplate = restTemplate;
    }

    @EnableOutgoingLogging
    public List<String> getInstances(String studyId, String serieId) {
        var findRequestModel = FindRequestModel.builder()
                .Level(INSTANCE)
                .Query(QueryRequestModel.builder()
                        .StudyInstanceUID(studyId)
                        .SeriesInstanceUID(serieId)
                        .build())
                .build();

        var uriComponents = UriComponentsBuilder.newInstance()
                .scheme(orthancServerConfig.getScheme())
                .host(orthancServerConfig.getHost())
                .port(orthancServerConfig.getPort())
                .path(orthancServerConfig.getEndpoints().get("find"))
                .build();

        HttpEntity<FindRequestModel> request = new HttpEntity<>(findRequestModel);

        if (Boolean.TRUE.equals(orthancServerConfig.getAuthEnabled())) {
            var headers = new HttpHeaders();
            headers.setBasicAuth(orthancServerConfig.getUsername(), orthancServerConfig.getPassword());
            request = new HttpEntity<>(findRequestModel, headers);
        }

        var opResult = Optional.ofNullable(restTemplate.exchange(uriComponents.toUriString(), HttpMethod.POST, request, new ParameterizedTypeReference<List<String>>() {}).getBody());
        var result = opResult.orElse(Collections.emptyList());
        log.info("Instances received: {}", result.size());
        log.info("Instances {}", Arrays.toString(result.toArray()));
        return  result;
    }

    @EnableOutgoingLogging
    public byte[] fetchInstance(String instance) {

        var uriComponents = UriComponentsBuilder.newInstance()
                .scheme(orthancServerConfig.getScheme())
                .host(orthancServerConfig.getHost())
                .port(orthancServerConfig.getPort())
                .path(orthancServerConfig.getEndpoints().get("instances"))
                .buildAndExpand(instance);

        HttpEntity<?> request = new HttpEntity<>(new HttpHeaders());
        if (Boolean.TRUE.equals(orthancServerConfig.getAuthEnabled())) {
            var headers = new HttpHeaders();
            headers.setBasicAuth(orthancServerConfig.getUsername(), orthancServerConfig.getPassword());
            request = new HttpEntity<>(headers);
        }

        ResponseEntity<byte[]> response = restTemplate.exchange(uriComponents.toUri(), HttpMethod.GET, request, byte[].class);


        return response.getBody();
    }
}
