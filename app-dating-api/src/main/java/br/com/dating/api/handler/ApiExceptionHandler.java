package br.com.dating.api.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

import br.com.dating.api.message.DefaultErrorCode;
import br.com.dating.api.message.ResponseError;

import br.com.dating.core.shared.error.ErrorDetailsCode;
import br.com.dating.core.shared.exception.AlreadyExistsException;
import br.com.dating.core.shared.exception.BadGatewayException;
import br.com.dating.core.shared.exception.BusinessException;

import br.com.dating.core.shared.exception.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
public class ApiExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

    private final MessageSource messageSource;

    public ApiExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, MissingRequestHeaderException.class})
    public ResponseEntity<ResponseError> handleValidationException(MethodArgumentNotValidException ex) {
        LOGGER.error("handleValidationException", ex);

        var details = new ArrayList<ResponseError.MessageErrorDetail>();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            var field = fieldError.getField();
            var code = formatErrorCode(fieldError, field);
            var message = messageSource.getMessage(fieldError, Locale.getDefault()).replaceAll("[. \\n]+", " ");
            var detail = new ResponseError.MessageErrorDetail(field, code, message);
            details.add(detail);
        }

        return ResponseEntity
            .badRequest()
            .body(new ResponseError(DefaultErrorCode.BAD_REQUEST, details));
    }

    private String formatErrorCode(FieldError fieldError, String field) {
        return field.concat(" ".concat(messageSource.getMessage(fieldError, Locale.getDefault())))
            .replaceAll("[. \\n]+", "_")
            .toUpperCase();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseError> handleConstraintException(ConstraintViolationException ex) {
        LOGGER.error("handleValidationException", ex);

        var details = new ArrayList<ResponseError.MessageErrorDetail>();

        for (ConstraintViolation<?> constraintViolation : ex.getConstraintViolations()) {
            var field = constraintViolation.getPropertyPath().toString();
            var code = constraintViolation.getMessage().replaceAll("[. \\n]+", "_").toUpperCase();
            var message = constraintViolation.getMessage();
            var detail = new ResponseError.MessageErrorDetail(field, code, message);
            details.add(detail);
        }

        return ResponseEntity
            .badRequest()
            .body(new ResponseError(DefaultErrorCode.BAD_REQUEST, details));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseError> httpMessageNotReadableException(HttpMessageNotReadableException ex) {
        LOGGER.error("httpMessageNotReadableException", ex);

        var error = DefaultErrorCode.REQUIRED_REQUEST_BODY_MISSING;

        var message = messageSource.getMessage(error.getMessageCode(), null, Locale.getDefault());

        return ResponseEntity
            .badRequest()
            .body(new ResponseError(error.getCode(), message));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ResponseError> handleApplicationException(BusinessException ex) {
        LOGGER.error("handleApplicationException", ex);

        var error = ex.getError();

        var message = messageSource.getMessage(error.getMessageCode(), null, Locale.getDefault());

        var details = new ArrayList<ResponseError.MessageErrorDetail>();

        if (ex.isToFormat()) {
            var valueArgs = ex.getMessageArguments();

            // Concatena o código na mensagem
            if (Arrays.stream(valueArgs).anyMatch(String.class::isInstance)) {
                message = String.format(message, valueArgs);
            }

            // Cria a seção de detalhes
            Arrays.stream(valueArgs)
                .filter(ErrorDetailsCode.ErrorDetail.class::isInstance)
                .map(ErrorDetailsCode.ErrorDetail.class::cast)
                .map(errorDetail -> new ResponseError.MessageErrorDetail(
                    errorDetail.field(),
                    errorDetail.value(),
                    ""
                ))
                .forEach(details::add);
        }

        return ResponseEntity
            .badRequest()
            .body(new ResponseError(error.getCode(), message, details));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ResponseError> handleNotFoundException(NotFoundException ex) {
        LOGGER.error("handleNotFoundException", ex);

        var error = ex.getError();

        var message = messageSource.getMessage(error.getMessageCode(), null, Locale.getDefault());

        return ResponseEntity
            .status(NOT_FOUND)
            .body(new ResponseError(error.getCode(), message));
    }

    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ResponseError> handlerException(IllegalArgumentException ex) {
        LOGGER.error("handlerException", ex);
        var error = ex.getMessage();

        return ResponseEntity
            .badRequest()
            .body(new ResponseError(DefaultErrorCode.BAD_REQUEST.getCode(), error));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ResponseError> noResourceFoundException(NoResourceFoundException ex) {
        LOGGER.error("noResourceFoundException", ex);

        return ResponseEntity
            .notFound()
            .build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseError> defaultException(Exception ex) {
        LOGGER.error("defaultException", ex);

        return ResponseEntity
            .internalServerError()
            .body(new ResponseError(DefaultErrorCode.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ResponseError> handleAlreadyExistsException(AlreadyExistsException ex) {
        LOGGER.error("handleAlreadyExistsException", ex);

        var error = ex.getError();

        var message = messageSource.getMessage(error.getMessageCode(), null, Locale.getDefault());

        var details = new ArrayList<ResponseError.MessageErrorDetail>();

        if (ex.isToFormat()) {
            var valueArgs = ex.getMessageArguments();

            // Concatena o código na mensagem
            if (Arrays.stream(valueArgs).anyMatch(String.class::isInstance)) {
                message = String.format(message, valueArgs);
            }

            // Cria a seção
            Arrays.stream(valueArgs)
                .filter(ErrorDetailsCode.ErrorDetail.class::isInstance)
                .map(ErrorDetailsCode.ErrorDetail.class::cast)
                .map(errorDetail -> new ResponseError.MessageErrorDetail(
                    errorDetail.field(),
                    errorDetail.value(),
                    ""
                ))
                .forEach(details::add);
        }

        return ResponseEntity
            .status(CONFLICT)
            .body(new ResponseError(error.getCode(), message, details));
    }

    @ExceptionHandler(BadGatewayException.class)
    public ResponseEntity<ResponseError> handleBadGatewayException(BadGatewayException ex) {
        LOGGER.error("handleBadGatewayException", ex);

        var error = ex.getError();

        var message = messageSource.getMessage(error.getMessageCode(), null, Locale.getDefault());

        return ResponseEntity
            .status(BAD_GATEWAY)
            .body(new ResponseError(error.getCode(), message));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ResponseError> handleMethodValidationException(HandlerMethodValidationException ex) {
        LOGGER.error("handleMethodValidationException", ex);

        var details = new ArrayList<ResponseError.MessageErrorDetail>();

        for (var parameterValidation : ex.getAllValidationResults()) {

            var detail = new ResponseError.MessageErrorDetail(
                parameterValidation.getMethodParameter().getParameterName(),
                formatErrorCode(Objects.requireNonNull(parameterValidation.getResolvableErrors().getFirst().getCodes())[0]),
                parameterValidation.getResolvableErrors().getFirst().getDefaultMessage());

            details.add(detail);
        }

        return ResponseEntity
            .badRequest()
            .body(new ResponseError(DefaultErrorCode.BAD_REQUEST, details));
    }

    private String formatErrorCode(String code) {
        int index = code.indexOf('.');
        if (index == -1) {
            return code;
        }
        return code.substring(0, index);
    }
}
