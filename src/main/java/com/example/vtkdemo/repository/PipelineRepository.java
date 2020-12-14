package com.example.vtkdemo.repository;

import com.example.vtkdemo.entity.Pipeline;
import com.example.vtkdemo.logging.EnableOutgoingLogging;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PipelineRepository extends JpaRepository<Pipeline, Long> {

    @Operation(summary = "Get all pipelines")
    @Counted(value = "findAll.count")
    @Timed(value = "findAll.time")
    @EnableOutgoingLogging @NotNull
    Page<Pipeline> findAll(@NotNull Pageable pageable);

    @Operation(summary = "Save or update a pipeline")
    @Counted(value = "save.count")
    @Timed(value = "save.time")
    @EnableOutgoingLogging
    <S extends Pipeline> @NotNull S save(@NotNull S s);

    @Operation(summary = "Get a pipeline by Id")
    @Counted(value = "findById.count")
    @Timed(value = "findById.time")
    @EnableOutgoingLogging @NotNull
    Optional<Pipeline> findById(@NotNull Long aLong);

    @Operation(summary = "Delete a pipeline")
    @Counted(value = "delete.count")
    @Timed(value = "delete.time")
    @EnableOutgoingLogging
    void delete(@NotNull Pipeline pipeline);
}
