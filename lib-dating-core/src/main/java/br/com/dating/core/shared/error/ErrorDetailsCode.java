package br.com.dating.core.shared.error;

import java.io.Serializable;

public enum ErrorDetailsCode {
    USER_CODE;

    public record ErrorDetail(String field, String value) implements Serializable {
    }
}
