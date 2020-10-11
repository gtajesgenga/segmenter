package com.example.vtkdemo.repository;

import com.example.vtkdemo.entity.Pipeline;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PipelineRepository extends JpaRepository<Pipeline, Long> {

    @Override
    @Operation(summary = "Get all pipelines")
    Page<Pipeline> findAll(Pageable pageable);

    @Operation(summary = "Save or update a pipeline")
    <S extends Pipeline> S save(S s);

    @Operation(summary = "Get a pipeline by Id")
    Optional<Pipeline> findById(Long aLong);

    @Operation(summary = "Delete a pipeline")
    void delete(Pipeline pipeline);
}
