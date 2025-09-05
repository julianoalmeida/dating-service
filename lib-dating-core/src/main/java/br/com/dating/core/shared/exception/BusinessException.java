package br.com.dating.core.shared.exception;

import java.io.Serial;

import br.com.dating.core.shared.error.BusinessErrorCode;

public class BusinessException extends Exception {

    @Serial
    private static final long serialVersionUID = 1154738598061887669L;
    private final BusinessErrorCode error;
    private Object[] messageArguments;

    public BusinessException(final BusinessErrorCode error) {
        super(error.getMessageCode());
        this.error = error;
    }

    public BusinessException(final BusinessErrorCode error, final Object... messageArguments) {
        this(error);
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
