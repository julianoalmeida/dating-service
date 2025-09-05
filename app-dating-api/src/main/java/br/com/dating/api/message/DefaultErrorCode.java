package br.com.dating.api.message;

import br.com.dating.core.shared.error.BusinessErrorCode;

public enum DefaultErrorCode implements BusinessErrorCode {

    BAD_REQUEST("bad.request"),

    REQUIRED_REQUEST_BODY_MISSING("required.request.body.missing"),

    NOT_FOUND("not.found"),

    INTERNAL_SERVER_ERROR("internal.server.error"),

    REQUIRED_ONE_PARAMETER("required.at.least.one.parameter");


    private final String messageCode;

    DefaultErrorCode(String messageCode) {
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
