package com.example.vtkdemo.repository;

import com.example.vtkdemo.entity.PipelineEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PipelineRepository extends JpaRepository<PipelineEntity, Long> {
}
