package br.com.dating.core.shared.exception;

import br.com.dating.core.shared.error.BusinessErrorCode;

import java.io.Serial;

public class BadGatewayException extends Exception {

    @Serial
    private static final long serialVersionUID = -1268669155852798022L;

    private final BusinessErrorCode error;

    public BadGatewayException(final BusinessErrorCode error) {
        super(error.getMessageCode());
        this.error = error;
    }

    public BusinessErrorCode getError() {
        return error;
    }
}
