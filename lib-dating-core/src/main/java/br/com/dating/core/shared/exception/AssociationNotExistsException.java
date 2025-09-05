package br.com.dating.core.shared.exception;

import java.io.Serial;

import br.com.dating.core.shared.error.BusinessErrorCode;


public class AssociationNotExistsException extends Exception {

    @Serial
    private static final long serialVersionUID = -6391615396492561539L;
    private final BusinessErrorCode error;

    public AssociationNotExistsException(final BusinessErrorCode error) {
        super(error.getMessageCode());
        this.error = error;
    }

    public BusinessErrorCode getError() {
        return error;
    }
}
