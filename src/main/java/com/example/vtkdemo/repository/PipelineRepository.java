package com.example.vtkdemo.repository;

import com.example.vtkdemo.entity.PipelineEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface PipelineRepository extends JpaRepository<PipelineEntity, Long> {

    Collection<PipelineEntity> findByNameContainingIgnoreCase(String filterText);
}
