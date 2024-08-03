package com.cedie.api.smartparking.exception;

public class NotFoundException extends RuntimeException {

    private static final String NOT_FOUND_MESSAGE = "The object %s was not found in the database.";

    public NotFoundException(Object object) {
        super(String.format(NOT_FOUND_MESSAGE, object));
    }
}
