package com.helpflow.support.domain.exception;

public class EntityNotFoundException extends DomainException {
    public EntityNotFoundException(String entityName, Object id) {
        super(String.format("%s not found with identifier: %s", entityName, id));
    }

    public EntityNotFoundException(String message) {
        super(message);
    }
}
