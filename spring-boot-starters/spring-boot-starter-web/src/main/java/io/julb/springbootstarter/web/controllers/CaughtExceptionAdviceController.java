/**
 * MIT License
 *
 * Copyright (c) 2017-2020 Julb
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.julb.springbootstarter.web.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.julb.library.dto.http.error.HttpErrorResponseDTO;
import io.julb.library.utility.exceptions.BadRequestException;
import io.julb.library.utility.exceptions.BaseException;
import io.julb.library.utility.exceptions.ConflictException;
import io.julb.library.utility.exceptions.ForbiddenException;
import io.julb.library.utility.exceptions.InternalServerErrorException;
import io.julb.library.utility.exceptions.NotFoundException;
import io.julb.library.utility.exceptions.NotImplementedException;
import io.julb.library.utility.exceptions.RemoteSystemClientErrorException;
import io.julb.library.utility.exceptions.RemoteSystemGatewayTimeoutException;
import io.julb.library.utility.exceptions.RemoteSystemServerErrorException;
import io.julb.library.utility.exceptions.RemoteSystemServiceUnavailableException;
import io.julb.library.utility.exceptions.ResourceNotFoundException;
import io.julb.library.utility.exceptions.ResourceStillReferencedException;
import io.julb.library.utility.exceptions.ServiceUnavailableException;
import io.julb.library.utility.exceptions.UnauthorizedException;
import io.julb.library.utility.http.HttpErrorResponseBuilder;
import io.julb.springbootstarter.core.messages.MessageSourceService;
import io.julb.springbootstarter.web.resolvers.search.exceptions.SearchTermSearchQueryParseException;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * A controller advice defined to catch all global exceptions.
 * <P>
 * @author Julb.
 */
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CaughtExceptionAdviceController {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CaughtExceptionAdviceController.class);

    /**
     * The message resource.
     */
    @Autowired
    private MessageSourceService messageSourceService;

    /**
     * Method that handles thrown {@link ConstraintViolationException}.
     * @param exception the exception that occurred.
     * @param request the HTTP Servlet request.
     * @return the error response.
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public final ResponseEntity<HttpErrorResponseDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception, HttpServletRequest request) {

        // For this exception, raise this HTTP Status.
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        LOGGER.info("Exception {} caught: {}. Root cause: {}", exception.getClass(), getErrorMessage(exception), (exception.getCause() != null ? exception.getCause().getMessage() : null));

        // Create the request
        HttpErrorResponseDTO errorResponse = HttpErrorResponseBuilder.defaultErrorResponse(httpStatus.value(), httpStatus.getReasonPhrase());
        FieldError fieldError = exception.getBindingResult().getFieldError();
        String errorMessage = String.format("%s: %s", fieldError.getField(), fieldError.getDefaultMessage());
        errorResponse.setMessage(errorMessage);

        // Set errors in stack
        List<FieldError> list = exception.getBindingResult().getFieldErrors();
        for (FieldError error : list) {
            String message = getErrorMessage(error.getDefaultMessage());
            errorResponse.getTrace().add(String.format("%s: %s", error.getField(), message));
        }

        return new ResponseEntity<>(errorResponse, httpStatus);
    }

    /**
     * Method that handles thrown {@link ConstraintViolationException}.
     * @param exception the exception that occurred.
     * @param request the HTTP Servlet request.
     * @return the error response.
     */
    @ExceptionHandler({BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public final ResponseEntity<HttpErrorResponseDTO> handleMethodArgumentNotValidException(BindException exception, HttpServletRequest request) {

        // For this exception, raise this HTTP Status.
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        LOGGER.info("Exception {} caught: {}. Root cause: {}", exception.getClass(), getErrorMessage(exception), (exception.getCause() != null ? exception.getCause().getMessage() : null));

        // Create the request
        HttpErrorResponseDTO errorResponse = HttpErrorResponseBuilder.defaultErrorResponse(httpStatus.value(), httpStatus.getReasonPhrase());
        FieldError fieldError = exception.getBindingResult().getFieldError();
        String errorMessage = String.format("%s: %s", fieldError.getField(), fieldError.getDefaultMessage());
        errorResponse.setMessage(errorMessage);

        // Set errors in stack
        List<FieldError> list = exception.getBindingResult().getFieldErrors();
        for (FieldError error : list) {
            String message = getErrorMessage(error.getDefaultMessage());
            errorResponse.getTrace().add(String.format("%s: %s", error.getField(), message));
        }

        return new ResponseEntity<>(errorResponse, httpStatus);
    }

    /**
     * Method that handles thrown {@link ConstraintViolationException}.
     * @param exception the exception that occurred.
     * @param request the HTTP Servlet request.
     * @return the error response.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public final ResponseEntity<HttpErrorResponseDTO> handleConstraintViolationException(ConstraintViolationException exception, HttpServletRequest request) {

        // For this exception, raise this HTTP Status.
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        LOGGER.info("Exception {} caught: {}. Root cause: {}", exception.getClass(), getErrorMessage(exception), (exception.getCause() != null ? exception.getCause().getMessage() : null));
        LOGGER.debug(HttpStatus.BAD_REQUEST.getReasonPhrase(), exception);

        // Create the request
        HttpErrorResponseDTO errorResponse = HttpErrorResponseBuilder.defaultErrorResponse(httpStatus.value(), httpStatus.getReasonPhrase());

        // Fill the trace
        Set<ConstraintViolation<?>> errors = exception.getConstraintViolations();
        for (ConstraintViolation<?> error : errors) {
            String message = getErrorMessage(error.getMessage());
            errorResponse.getTrace().add(String.format("%s: %s", error.getPropertyPath(), message));
        }

        // Set the message with the first error
        errorResponse.setMessage(errors.size() > 0 ? getErrorMessage(errors.iterator().next().getMessage()) : getErrorMessage(exception));

        return new ResponseEntity<>(errorResponse, httpStatus);
    }

    /**
     * Method that handles thrown {@link BadRequestException}.
     * @param exception the exception that occurred.
     * @param request the HTTP Servlet request.
     * @return the error response.
     */
    @ExceptionHandler({ServletRequestBindingException.class, ValidationException.class, HttpMessageNotReadableException.class, JsonProcessingException.class, BadRequestException.class, MethodArgumentTypeMismatchException.class,
        UnsupportedOperationException.class, ResourceStillReferencedException.class, SearchTermSearchQueryParseException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public final ResponseEntity<HttpErrorResponseDTO> handleGenericBadRequestException(Exception exception, HttpServletRequest request) {

        // For this exception, raise this HTTP Status.
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        LOGGER.info("Exception {} caught: {}. Root cause: {}", exception.getClass(), getErrorMessage(exception), (exception.getCause() != null ? exception.getCause().getMessage() : ""));
        LOGGER.debug(HttpStatus.BAD_REQUEST.getReasonPhrase(), exception);

        // Create the request
        HttpErrorResponseDTO errorResponse = HttpErrorResponseBuilder.defaultErrorResponse(httpStatus.value(), httpStatus.getReasonPhrase());
        errorResponse.setMessage(getErrorMessage(exception));
        return new ResponseEntity<>(errorResponse, httpStatus);
    }

    /**
     * Method that handles thrown {@link ForbiddenException}.
     * @param exception the exception that occurred.
     * @param request the HTTP Servlet request.
     * @return the error response.
     */
    @ExceptionHandler({UnauthorizedException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public final ResponseEntity<HttpErrorResponseDTO> handleUnauthorizedException(UnauthorizedException exception, HttpServletRequest request) {

        // For this exception, raise this HTTP Status.
        HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;
        LOGGER.info("Exception {} caught: {}. Root cause: {}", exception.getClass(), getErrorMessage(exception), (exception.getCause() != null ? exception.getCause().getMessage() : null));
        LOGGER.debug(httpStatus.getReasonPhrase(), exception);

        // Create the request
        HttpErrorResponseDTO errorResponse = HttpErrorResponseBuilder.defaultErrorResponse(httpStatus.value(), httpStatus.getReasonPhrase());
        errorResponse.setMessage(getErrorMessage(exception));
        return new ResponseEntity<>(errorResponse, httpStatus);
    }

    /**
     * Method that handles thrown {@link ForbiddenException}.
     * @param exception the exception that occurred.
     * @param request the HTTP Servlet request.
     * @return the error response.
     */
    @ExceptionHandler({ForbiddenException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public final ResponseEntity<HttpErrorResponseDTO> handleForbiddenException(ForbiddenException exception, HttpServletRequest request) {

        // For this exception, raise this HTTP Status.
        HttpStatus httpStatus = HttpStatus.FORBIDDEN;
        LOGGER.info("Exception {} caught: {}. Root cause: {}", exception.getClass(), getErrorMessage(exception), (exception.getCause() != null ? exception.getCause().getMessage() : null));
        LOGGER.debug(HttpStatus.FORBIDDEN.getReasonPhrase(), exception);

        // Create the request
        HttpErrorResponseDTO errorResponse = HttpErrorResponseBuilder.defaultErrorResponse(httpStatus.value(), httpStatus.getReasonPhrase());
        errorResponse.setMessage(getErrorMessage(exception));
        return new ResponseEntity<>(errorResponse, httpStatus);
    }

    /**
     * Method that handles thrown {@link ResourceNotFoundException}.
     * @param exception the exception that occurred.
     * @param request the HTTP Servlet request.
     * @return the error response.
     */
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public final ResponseEntity<HttpErrorResponseDTO> handleNotFoundException(NotFoundException exception, HttpServletRequest request) {

        // For this exception, raise this HTTP Status.
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        LOGGER.info("Exception {} caught: {}. Root cause: {}", exception.getClass(), getErrorMessage(exception), (exception.getCause() != null ? exception.getCause().getMessage() : null));
        LOGGER.debug(HttpStatus.NOT_FOUND.getReasonPhrase(), exception);

        // Create the request
        HttpErrorResponseDTO errorResponse = HttpErrorResponseBuilder.defaultErrorResponse(httpStatus.value(), httpStatus.getReasonPhrase());
        errorResponse.setMessage(getErrorMessage(exception));
        return new ResponseEntity<>(errorResponse, httpStatus);
    }

    /**
     * Method that handles thrown {@link HttpRequestMethodNotSupportedException}.
     * @param exception the exception that occurred.
     * @param request the HTTP Servlet request.
     * @return the error response.
     */
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public final ResponseEntity<HttpErrorResponseDTO> handlMethodNotAllowedException(Exception exception, HttpServletRequest request) {

        // For this exception, raise this HTTP Status.
        HttpStatus httpStatus = HttpStatus.METHOD_NOT_ALLOWED;
        LOGGER.info("Exception {} caught: {}. Root cause: {}", exception.getClass(), getErrorMessage(exception), (exception.getCause() != null ? exception.getCause().getMessage() : ""));
        LOGGER.debug(HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase(), exception);

        // Create the request
        HttpErrorResponseDTO errorResponse = HttpErrorResponseBuilder.defaultErrorResponse(httpStatus.value(), httpStatus.getReasonPhrase());
        errorResponse.setMessage(httpStatus.getReasonPhrase());
        return new ResponseEntity<>(errorResponse, httpStatus);
    }

    /**
     * Method that handles thrown {@link ConflictException}.
     * @param exception the exception that occurred.
     * @param request the HTTP Servlet request.
     * @return the error response.
     */
    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public final ResponseEntity<HttpErrorResponseDTO> handleConflictException(ConflictException exception, HttpServletRequest request) {

        // For this exception, raise this HTTP Status.
        HttpStatus httpStatus = HttpStatus.CONFLICT;
        LOGGER.info("Exception {} caught: {}. Root cause: {}", exception.getClass(), getErrorMessage(exception), (exception.getCause() != null ? exception.getCause().getMessage() : null));
        LOGGER.debug(HttpStatus.CONFLICT.getReasonPhrase(), exception);

        // Create the request
        HttpErrorResponseDTO errorResponse = HttpErrorResponseBuilder.defaultErrorResponse(httpStatus.value(), httpStatus.getReasonPhrase());
        errorResponse.setMessage(getErrorMessage(exception));
        return new ResponseEntity<>(errorResponse, httpStatus);
    }

    /**
     * Method that handles thrown {@link HttpRequestMethodNotSupportedException}.
     * @param exception the exception that occurred.
     * @param request the HTTP Servlet request.
     * @return the error response.
     */
    @ExceptionHandler({HttpMediaTypeNotSupportedException.class})
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public final ResponseEntity<HttpErrorResponseDTO> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException exception, HttpServletRequest request) {

        // For this exception, raise this HTTP Status.
        HttpStatus httpStatus = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
        LOGGER.info("Exception {} caught: {}. Root cause: {}", exception.getClass(), getErrorMessage(exception), (exception.getCause() != null ? exception.getCause().getMessage() : ""));
        LOGGER.debug(HttpStatus.UNSUPPORTED_MEDIA_TYPE.getReasonPhrase(), exception);

        // Create the request
        HttpErrorResponseDTO errorResponse = HttpErrorResponseBuilder.defaultErrorResponse(httpStatus.value(), httpStatus.getReasonPhrase());
        errorResponse.setMessage(httpStatus.getReasonPhrase());
        return new ResponseEntity<>(errorResponse, httpStatus);
    }

    /**
     * Method that handles thrown {@link RemoteSystemClientErrorException} and {@link InternalServerErrorException}.
     * @param exception the exception that occurred.
     * @param request the HTTP Servlet request.
     * @return the error response.
     */
    @ExceptionHandler({RemoteSystemClientErrorException.class, InternalServerErrorException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public final ResponseEntity<HttpErrorResponseDTO> handleRemoteSystemClientErrorException(Exception exception, HttpServletRequest request) {

        // For this exception, raise this HTTP Status.
        HttpStatus internalServerError = HttpStatus.INTERNAL_SERVER_ERROR;
        LOGGER.error(getErrorMessage(exception), exception);

        // Create the request
        HttpErrorResponseDTO status = HttpErrorResponseBuilder.defaultErrorResponse(internalServerError.value(), internalServerError.getReasonPhrase());
        return new ResponseEntity<>(status, internalServerError);
    }

    /**
     * Method that handles thrown {@link NotImplementedException}.
     * @param exception the exception that occurred.
     * @param request the HTTP Servlet request.
     * @return the error response.
     */
    @ExceptionHandler(NotImplementedException.class)
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public final ResponseEntity<HttpErrorResponseDTO> handleNotImplemented(NotImplementedException exception, HttpServletRequest request) {

        // For this exception, raise this HTTP Status.
        HttpStatus notImplementedError = HttpStatus.NOT_IMPLEMENTED;
        LOGGER.error(getErrorMessage(exception), exception);

        // Create the request
        HttpErrorResponseDTO status = HttpErrorResponseBuilder.defaultErrorResponse(notImplementedError.value(), notImplementedError.getReasonPhrase());
        return new ResponseEntity<>(status, notImplementedError);
    }

    /**
     * Method that handles thrown {@link RemoteSystemServerErrorException}.
     * @param exception the exception that occurred.
     * @param request the HTTP Servlet request.
     * @return the error response.
     */
    @ExceptionHandler(RemoteSystemServerErrorException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public final ResponseEntity<HttpErrorResponseDTO> handleRemoteSystemFailure(RemoteSystemServerErrorException exception, HttpServletRequest request) {

        // For this exception, raise this HTTP Status.
        HttpStatus badGatewayError = HttpStatus.BAD_GATEWAY;
        LOGGER.error(getErrorMessage(exception), exception);

        // Create the request
        HttpErrorResponseDTO status = HttpErrorResponseBuilder.defaultErrorResponse(badGatewayError.value(), badGatewayError.getReasonPhrase());
        return new ResponseEntity<>(status, badGatewayError);
    }

    /**
     * Method that handles thrown {@link RemoteSystemServiceUnavailableException}.
     * @param exception the exception that occurred.
     * @param request the HTTP Servlet request.
     * @return the error response.
     */
    @ExceptionHandler(RemoteSystemServiceUnavailableException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public final ResponseEntity<HttpErrorResponseDTO> handleRemoteSystemAccessException(RemoteSystemServiceUnavailableException exception, HttpServletRequest request) {

        // For this exception, raise this HTTP Status.
        HttpStatus serviceUnavailableStatus = HttpStatus.BAD_GATEWAY;
        LOGGER.error(getErrorMessage(exception), exception);

        // Create the request
        HttpErrorResponseDTO status = HttpErrorResponseBuilder.defaultErrorResponse(serviceUnavailableStatus.value(), serviceUnavailableStatus.getReasonPhrase());
        return new ResponseEntity<>(status, serviceUnavailableStatus);
    }

    /**
     * Method that handles thrown {@link ServiceUnavailableException}.
     * @param exception the exception that occurred.
     * @param request the HTTP Servlet request.
     * @return the error response.
     */
    @ExceptionHandler(ServiceUnavailableException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public final ResponseEntity<HttpErrorResponseDTO> handleNotImplemented(ServiceUnavailableException exception, HttpServletRequest request) {

        // For this exception, raise this HTTP Status.
        HttpStatus notImplementedError = HttpStatus.SERVICE_UNAVAILABLE;
        LOGGER.error(getErrorMessage(exception), exception);

        // Create the request
        HttpErrorResponseDTO status = HttpErrorResponseBuilder.defaultErrorResponse(notImplementedError.value(), notImplementedError.getReasonPhrase());
        return new ResponseEntity<>(status, notImplementedError);
    }

    /**
     * Method that handles thrown {@link RemoteSystemGatewayTimeoutException}.
     * @param exception the exception that occurred.
     * @param request the HTTP Servlet request.
     * @return the error response.
     */
    @ExceptionHandler(RemoteSystemGatewayTimeoutException.class)
    @ResponseStatus(HttpStatus.GATEWAY_TIMEOUT)
    public final ResponseEntity<HttpErrorResponseDTO> handleRemoteSystemTimeoutException(RemoteSystemGatewayTimeoutException exception, HttpServletRequest request) {

        // For this exception, raise this HTTP Status.
        HttpStatus gatewayTimeoutError = HttpStatus.GATEWAY_TIMEOUT;
        LOGGER.error(getErrorMessage(exception), exception);

        // Create the request
        HttpErrorResponseDTO status = HttpErrorResponseBuilder.defaultErrorResponse(gatewayTimeoutError.value(), gatewayTimeoutError.getReasonPhrase());
        return new ResponseEntity<>(status, gatewayTimeoutError);
    }

    /**
     * Translate the exception message.
     * @param exception the exception message.
     * @return the message localized.
     */
    private String getErrorMessage(Exception exception) {
        if (exception instanceof BaseException) {
            BaseException e = (BaseException) exception;
            return getErrorMessage(e.getMessage(), e.getMessageArgs());
        } else {
            return getErrorMessage(exception.getMessage());
        }
    }

    /**
     * Translate the message if it's in curly brackets.
     * @param message the message.
     * @return the message translated.
     */
    private String getErrorMessage(String message, Object... args) {
        if (message.startsWith("{") && message.endsWith("}")) {
            String code = message.substring(1, message.length() - 1);
            return messageSourceService.getMessage(code, args, code, LocaleContextHolder.getLocale());
        } else {
            return message;
        }
    }
}
