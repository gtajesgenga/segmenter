package com.example.vtkdemo.service;

import com.example.vtkdemo.client.OrthancClient;
import com.example.vtkdemo.config.ApplicationConfig;
import com.example.vtkdemo.entity.Pipeline;
import com.example.vtkdemo.repository.PipelineRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class PipelineService {

    private final PipelineRepository pipelineRepository;

    @Autowired public PipelineService(ApplicationConfig applicationConfig, OrthancClient orthancClient, PipelineRepository pipelineRepository) {
        this.pipelineRepository = pipelineRepository;
    }

    public Pipeline createPipeline(Pipeline pipeline) {
        return pipelineRepository.save(Pipeline.builder()
                .name(pipeline.getName())
                .filters(pipeline.getFilters())
                .build());
    }

    public List<Pipeline> findAll() {
        return pipelineRepository.findAll();
    }

    public Optional<Pipeline> findById(Long id) {
        return pipelineRepository.findById(id);
    }

    public void deleteById(Long id) {
        pipelineRepository.deleteById(id);
    }

    public Optional<Pipeline> updateById(Long id, Pipeline pipeline) {
        Optional<Pipeline> old = findById(id);

        if (old.isPresent()) {
            Pipeline entity = old.get();
            entity.setName(pipeline.getName());
            entity.setFilters(pipeline.getFilters());
            return Optional.of(pipelineRepository.save(entity));
        }
        return old;
    }
}
