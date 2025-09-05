package br.com.dating.core.shared.exception;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class EntityAlreadyExistsException extends Exception {

    @Serial
    private static final long serialVersionUID = 5250201375157278732L;
    private final Class<?> entityClass;

    private final Serializable entityId;

    private final String searchValue;

    private final String searchField;

    public EntityAlreadyExistsException(Class<?> entityClass, Serializable entityId, String searchField,
                                        String searchValue) {
        this.entityClass = Objects.requireNonNull(entityClass);
        this.entityId = Objects.requireNonNull(entityId);
        this.searchValue = Objects.requireNonNull(searchValue);
        this.searchField = Objects.requireNonNull(searchField);
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public Serializable getEntityId() {
        return entityId;
    }

    public String getSearchField() {
        return searchField;
    }

    public Object getSearchValue() {
        return searchValue;
    }
}
