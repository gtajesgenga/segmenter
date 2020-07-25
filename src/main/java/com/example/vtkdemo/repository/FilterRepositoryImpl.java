package com.example.vtkdemo.repository;

import org.springframework.stereotype.Repository;

import com.example.vtkdemo.entity.Filter;

import java.util.Optional;

@Repository
public class FilterRepositoryImpl implements FilterRepository<Filter, String> {

    @Override public Iterable<Filter> findAll() {
        return null;
    }

    @Override public Optional<Filter> findByName(String var1) {
        return Optional.empty();
    }
}
