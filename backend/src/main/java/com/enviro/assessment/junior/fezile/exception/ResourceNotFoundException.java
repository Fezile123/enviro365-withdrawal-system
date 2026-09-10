package com.enviro.assessment.junior.fezile.exception;

/**
 * Thrown when a requested resource (investor, portfolio, product, etc.)
 * does not exist. Translated to a 404 response by the controller layer.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
