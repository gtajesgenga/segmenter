package com.example.vtkdemo.service;

import com.example.vtkdemo.client.OrthancClient;
import com.example.vtkdemo.config.ApplicationConfig;
import com.example.vtkdemo.entity.PipelineEntity;
import com.example.vtkdemo.model.PipelineRequest;
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

    public PipelineEntity createPipeline(PipelineRequest pipeline) {
        return pipelineRepository.save(PipelineEntity.builder()
                .name(pipeline.getName())
                .pipelineDto(pipeline.getPipelineDto())
                .build());
    }

    public List<PipelineEntity> findAll() {
        return pipelineRepository.findAll();
    }

    public Optional<PipelineEntity> findById(Long id) {
        return pipelineRepository.findById(id);
    }

    public void deleteById(Long id) {
        pipelineRepository.deleteById(id);
    }

    public Optional<PipelineEntity> updateById(Long id, PipelineRequest pipeline) {
        Optional<PipelineEntity> old = findById(id);

        if (old.isPresent()) {
            PipelineEntity entity = old.get();
            entity.setName(pipeline.getName());
            entity.setPipelineDto(pipeline.getPipelineDto());
            return Optional.of(pipelineRepository.save(entity));
        }
        return old;
    }
}
