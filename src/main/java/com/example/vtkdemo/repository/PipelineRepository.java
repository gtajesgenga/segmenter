package com.example.vtkdemo.repository;

import com.example.vtkdemo.entity.Pipeline;
import com.example.vtkdemo.logging.EnableOutgoingLogging;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PipelineRepository extends JpaRepository<Pipeline, Long> {

    @Operation(summary = "Get all pipelines")
    @Timed
    @Counted
    @EnableOutgoingLogging
    Page<Pipeline> findAll(Pageable pageable);

    @Operation(summary = "Save or update a pipeline")
    @Timed
    @Counted
    @EnableOutgoingLogging
    <S extends Pipeline> S save(S s);

    @Operation(summary = "Get a pipeline by Id")
    @Timed
    @Counted
    @EnableOutgoingLogging
    Optional<Pipeline> findById(Long aLong);

    @Operation(summary = "Delete a pipeline")
    @Timed
    @Counted
    @EnableOutgoingLogging
    void delete(Pipeline pipeline);
}
