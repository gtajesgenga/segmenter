package com.example.vtkdemo.repository;

import com.example.vtkdemo.entity.Pipeline;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PipelineRepository extends JpaRepository<Pipeline, Long> {
}
