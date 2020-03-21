package com.example.vtkdemo.client;

import com.example.vtkdemo.client.model.FindRequestModel;
import com.example.vtkdemo.client.model.QueryRequestModel;
import com.example.vtkdemo.config.OrthancServerConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

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

    public List<String> getInstances(String studyId, String serieId) {
        FindRequestModel findRequestModel = FindRequestModel.builder()
                .Level(INSTANCE)
                .Query(QueryRequestModel.builder()
                        .StudyInstanceUID(studyId)
                        .SeriesInstanceUID(serieId)
                        .build())
                .build();

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme(orthancServerConfig.getScheme())
                .host(orthancServerConfig.getHost())
                .port(orthancServerConfig.getPort())
                .path(orthancServerConfig.getEndpoints().get("find"))
                .build();

        HttpEntity<FindRequestModel> request = new HttpEntity<>(findRequestModel);

        if (orthancServerConfig.getAuthEnabled()) {
            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(orthancServerConfig.getUsername(), orthancServerConfig.getPassword());
            request = new HttpEntity<>(findRequestModel, headers);
        }

        return restTemplate.postForObject(uriComponents.toUriString(), request, List.class);
    }

    public byte[] fetchInstance(String instance) {

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme(orthancServerConfig.getScheme())
                .host(orthancServerConfig.getHost())
                .port(orthancServerConfig.getPort())
                .path(orthancServerConfig.getEndpoints().get("instances"))
                .buildAndExpand(instance);

        HttpEntity request = new HttpEntity<>(new HttpHeaders());
        if (orthancServerConfig.getAuthEnabled()) {
            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(orthancServerConfig.getUsername(), orthancServerConfig.getPassword());
            request = new HttpEntity<>(headers);
        }

        ResponseEntity<byte[]> response = restTemplate.exchange(uriComponents.toUri(), HttpMethod.GET, request, byte[].class);


        return response.getBody();
    }
}
