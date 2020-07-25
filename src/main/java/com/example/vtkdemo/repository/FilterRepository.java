package com.example.vtkdemo.repository;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FilterRepository<T, ID> {

    Iterable<T> findAll();

    Optional<T> findByName(String var1);
}
