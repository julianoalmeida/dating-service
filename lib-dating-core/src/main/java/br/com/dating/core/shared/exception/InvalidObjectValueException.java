package br.com.dating.core.shared.exception;

import java.io.Serial;
import java.util.Objects;

/**
 * Exceção que indica que algum dado informado não é valido, pode-se usar também
 * para valores não encontrados
 */
public class InvalidObjectValueException extends Exception {

    @Serial
    private static final long serialVersionUID = -1702537653413059636L;
    private final String objectName;
    private final String objectValue;

    public InvalidObjectValueException(String objectName, String objectValue) {
        this.objectName = Objects.requireNonNull(objectName);
        this.objectValue = Objects.requireNonNull(objectValue);
    }

    public String getObjectName() {
        return objectName;
    }

    public String getObjectValue() {
        return objectValue;
    }
}
