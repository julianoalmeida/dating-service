package br.com.dating.core.shared.exception;

import java.io.Serial;

import br.com.dating.core.shared.error.BusinessErrorCode;

public class AlreadyExistsException extends Exception {

    @Serial
    private static final long serialVersionUID = -7532640514000466428L;
    private final BusinessErrorCode error;
    private Object[] messageArguments;

    public AlreadyExistsException(final BusinessErrorCode error) {
        super(error.getMessageCode());
        this.error = error;
    }

    public AlreadyExistsException(final BusinessErrorCode error, final Object... messageArguments) {
        super(error.getMessageCode());
        this.error = error;
        this.messageArguments = messageArguments;
    }

    public BusinessErrorCode getError() {
        return error;
    }

    public Object[] getMessageArguments() {
        return messageArguments;
    }

    public boolean isToFormat() {
        return messageArguments != null && messageArguments.length > 0;
    }
}
