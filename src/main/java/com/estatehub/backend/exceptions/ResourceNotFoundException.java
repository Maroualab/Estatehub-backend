package com.estatehub.backend.exceptions;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, Long id) {
        super(resourceName + " avec l'ID " + id + " introuvable");
    }

    public ResourceNotFoundException(String resourceName, String identifier) {
        super(resourceName + " avec l'identifiant '" + identifier + "' introuvable");
    }
}
