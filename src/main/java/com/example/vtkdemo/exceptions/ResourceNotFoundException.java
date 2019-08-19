package com.example.vtkdemo.exceptions;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String identifier, String resource, String identifierName) {
        super("Could not find " + resource + " with " + identifierName + ": " + identifier);
    }
}
