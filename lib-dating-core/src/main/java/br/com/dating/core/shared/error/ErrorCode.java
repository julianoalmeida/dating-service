package br.com.dating.core.shared.error;

public enum ErrorCode implements BusinessErrorCode {

    USER_NOT_FOUND("user.not.found"),
    AUTH_INVALID_TOKEN("auth.invalid.token");

    private final String messageCode;

    ErrorCode(String messageCode) {
        this.messageCode = messageCode;
    }

    @Override
    public String getCode() {
        return this.name();
    }

    @Override
    public String getMessageCode() {
        return messageCode;
    }
}
