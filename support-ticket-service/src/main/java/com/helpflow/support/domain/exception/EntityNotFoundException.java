package com.helpflow.support.domain.exception;

public class EntityNotFoundException extends DomainException {
    public EntityNotFoundException(String entityName, Object id) {
        super(String.format("No se encontró %s con identificador: %s", entityName, id));
    }

    public EntityNotFoundException(String message) {
        super(message);
    }
}
