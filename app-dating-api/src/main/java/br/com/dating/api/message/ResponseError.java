package br.com.dating.api.message;

import java.io.Serializable;
import java.util.List;

import br.com.dating.core.shared.error.BusinessErrorCode;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ResponseError(Message error) implements Serializable {

    public ResponseError(String code, String message) {
        this(new Message(code, message, List.of()));
    }

    public ResponseError(BusinessErrorCode errorCode) {
        this(new Message(errorCode.getCode(), errorCode.getMessageCode(), List.of()));
    }

    public ResponseError(String code, String message, List<MessageErrorDetail> details) {
        this(new Message(code, message, details));
    }

    public ResponseError(BusinessErrorCode errorCode, List<MessageErrorDetail> details) {
        this(new Message(errorCode.getCode(), errorCode.getMessageCode(), details));
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record Message(String code, String message, List<MessageErrorDetail> details) implements Serializable {
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record MessageErrorDetail(String field, String code, String message) implements Serializable {
    }

}
