package br.com.dating.core.shared.exception;

import java.io.Serial;

import br.com.dating.core.shared.error.BusinessErrorCode;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundException extends Exception {

    @Serial
    private static final long serialVersionUID = -6153686857396718243L;
    private final BusinessErrorCode error;
    private Object[] messageArguments;

    public NotFoundException(final BusinessErrorCode error) {
        super(error.getMessageCode());
        this.error = error;
    }

    public NotFoundException(final BusinessErrorCode error, final Object... messageArguments) {
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
