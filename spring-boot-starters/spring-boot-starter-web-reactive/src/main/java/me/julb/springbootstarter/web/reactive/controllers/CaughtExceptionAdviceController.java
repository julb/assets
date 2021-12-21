/**
 * MIT License
 *
 * Copyright (c) 2017-2021 Julb
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
package me.julb.springbootstarter.web.reactive.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import lombok.extern.slf4j.Slf4j;

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
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ServerWebExchange;

import me.julb.library.dto.http.error.HttpErrorResponseDTO;
import me.julb.library.utility.exceptions.BadRequestException;
import me.julb.library.utility.exceptions.BaseException;
import me.julb.library.utility.exceptions.ConflictException;
import me.julb.library.utility.exceptions.ForbiddenException;
import me.julb.library.utility.exceptions.InternalServerErrorException;
import me.julb.library.utility.exceptions.NotFoundException;
import me.julb.library.utility.exceptions.NotImplementedException;
import me.julb.library.utility.exceptions.RemoteSystemClientErrorException;
import me.julb.library.utility.exceptions.RemoteSystemGatewayTimeoutException;
import me.julb.library.utility.exceptions.RemoteSystemServerErrorException;
import me.julb.library.utility.exceptions.RemoteSystemServiceUnavailableException;
import me.julb.library.utility.exceptions.ResourceNotFoundException;
import me.julb.library.utility.exceptions.ResourceStillReferencedException;
import me.julb.library.utility.exceptions.ServiceUnavailableException;
import me.julb.library.utility.exceptions.UnauthorizedException;
import me.julb.library.utility.http.HttpErrorResponseBuilder;
import me.julb.springbootstarter.core.context.ContextConstants;
import me.julb.springbootstarter.core.messages.MessageSourceService;
import me.julb.springbootstarter.opentracing.utility.TracingContextUtility;
import me.julb.springbootstarter.web.resolvers.search.exceptions.SearchTermSearchQueryParseException;

import brave.Tracer;
import reactor.core.publisher.Mono;

/**
 * A controller advice defined to catch all global exceptions.
 * <br>
 * @author Julb.
 */
@ControllerAdvice
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CaughtExceptionAdviceController {

    /**
     * The message resource.
     */
    @Autowired
    private MessageSourceService messageSourceService;

    /**
     * The tracer attribute.
     */
    @Autowired
    private Tracer tracer;

    /**
     * Method that handles thrown {@link ConstraintViolationException}.
     * @param exception the exception that occurred.
     * @param exchange the exchange.
     * @return the error response.
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public final Mono<ResponseEntity<HttpErrorResponseDTO>> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception, ServerWebExchange exchange) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // For this exception, raise this HTTP Status.
            HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
            LOGGER.info("Exception {} caught: {}. Root cause: {}", exception.getClass(), getErrorMessage(tm, exception), (exception.getCause() != null ? exception.getCause().getMessage() : null));

            // Create the request
            HttpErrorResponseDTO errorResponse = HttpErrorResponseBuilder.defaultErrorResponse(
                httpStatus.value(), 
                httpStatus.getReasonPhrase(), 
                exchange.getRequest().getPath().toString(),
                TracingContextUtility.asMap(tracer.currentSpan().context()));
            FieldError fieldError = exception.getBindingResult().getFieldError();
            String errorMessage = String.format("%s: %s", fieldError.getField(), fieldError.getDefaultMessage());
            errorResponse.setMessage(errorMessage);

            // Set errors in stack
            List<FieldError> list = exception.getBindingResult().getFieldErrors();
            for (FieldError error : list) {
                String message = getErrorMessage(tm, error.getDefaultMessage());
                errorResponse.getTrace().add(String.format("%s: %s", error.getField(), message));
            }

            return Mono.just(new ResponseEntity<>(errorResponse, httpStatus));
        });
    }

    /**
     * Method that handles thrown {@link ConstraintViolationException}.
     * @param exception the exception that occurred.
     * @param exchange the exchange.
     * @return the error response.
     */
    @ExceptionHandler({BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public final Mono<ResponseEntity<HttpErrorResponseDTO>> handleMethodArgumentNotValidException(BindException exception, ServerWebExchange exchange) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // For this exception, raise this HTTP Status.
            HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
            LOGGER.info("Exception {} caught: {}. Root cause: {}", exception.getClass(), getErrorMessage(tm, exception), (exception.getCause() != null ? exception.getCause().getMessage() : null));

            // Create the request
            HttpErrorResponseDTO errorResponse = HttpErrorResponseBuilder.defaultErrorResponse(
                httpStatus.value(), 
                httpStatus.getReasonPhrase(), 
                exchange.getRequest().getPath().toString(),
                TracingContextUtility.asMap(tracer.currentSpan().context()));
            FieldError fieldError = exception.getBindingResult().getFieldError();
            String errorMessage = String.format("%s: %s", fieldError.getField(), fieldError.getDefaultMessage());
            errorResponse.setMessage(errorMessage);

            // Set errors in stack
            List<FieldError> list = exception.getBindingResult().getFieldErrors();
            for (FieldError error : list) {
                String message = getErrorMessage(tm, error.getDefaultMessage());
                errorResponse.getTrace().add(String.format("%s: %s", error.getField(), message));
            }

            return Mono.just(new ResponseEntity<>(errorResponse, httpStatus));
        });
    }

    /**
     * Method that handles thrown {@link WebExchangeBindException}.
     * @param exception the exception that occurred.
     * @param exchange the exchange.
     * @return the error response.
     */
    @ExceptionHandler({WebExchangeBindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public final Mono<ResponseEntity<HttpErrorResponseDTO>> handleWebExchangeBindException(WebExchangeBindException exception, ServerWebExchange exchange) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // For this exception, raise this HTTP Status.
            HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
            LOGGER.info("Exception {} caught: {}. Root cause: {}", exception.getClass(), getErrorMessage(tm, exception), (exception.getCause() != null ? exception.getCause().getMessage() : null));

            // Create the request
            HttpErrorResponseDTO errorResponse = HttpErrorResponseBuilder.defaultErrorResponse(
                httpStatus.value(), 
                httpStatus.getReasonPhrase(), 
                exchange.getRequest().getPath().toString(),
                TracingContextUtility.asMap(tracer.currentSpan().context()));
            FieldError fieldError = exception.getBindingResult().getFieldError();
            String errorMessage = String.format("%s: %s", fieldError.getField(), fieldError.getDefaultMessage());
            errorResponse.setMessage(errorMessage);

            // Set errors in stack
            List<FieldError> list = exception.getBindingResult().getFieldErrors();
            for (FieldError error : list) {
                String message = getErrorMessage(tm, error.getDefaultMessage());
                errorResponse.getTrace().add(String.format("%s: %s", error.getField(), message));
            }

            return Mono.just(new ResponseEntity<>(errorResponse, httpStatus));
        });
    }

    /**
     * Method that handles thrown {@link ConstraintViolationException}.
     * @param exception the exception that occurred.
     * @param exchange the exchange.
     * @return the error response.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public final Mono<ResponseEntity<HttpErrorResponseDTO>> handleConstraintViolationException(ConstraintViolationException exception, ServerWebExchange exchange) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // For this exception, raise this HTTP Status.
            HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
            LOGGER.info("Exception {} caught: {}. Root cause: {}", exception.getClass(), getErrorMessage(tm, exception), (exception.getCause() != null ? exception.getCause().getMessage() : null));
            LOGGER.debug(HttpStatus.BAD_REQUEST.getReasonPhrase(), exception);

            // Create the request
            HttpErrorResponseDTO errorResponse = HttpErrorResponseBuilder.defaultErrorResponse(
                httpStatus.value(), 
                httpStatus.getReasonPhrase(), 
                exchange.getRequest().getPath().toString(),
                TracingContextUtility.asMap(tracer.currentSpan().context()));

            // Fill the trace
            Set<ConstraintViolation<?>> errors = exception.getConstraintViolations();
            for (ConstraintViolation<?> error : errors) {
                String message = getErrorMessage(tm, error.getMessage());
                errorResponse.getTrace().add(String.format("%s: %s", error.getPropertyPath(), message));
            }

            // Set the message with the first error
            errorResponse.setMessage(errors.size() > 0 ? getErrorMessage(tm, errors.iterator().next().getMessage()) : getErrorMessage(tm, exception));

            return Mono.just(new ResponseEntity<>(errorResponse, httpStatus));
        });
    }

    /**
     * Method that handles thrown {@link BadRequestException}.
     * @param exception the exception that occurred.
     * @param exchange the exchange.
     * @return the error response.
     */
    @ExceptionHandler({ValidationException.class, HttpMessageNotReadableException.class, JsonProcessingException.class, BadRequestException.class, MethodArgumentTypeMismatchException.class,
        UnsupportedOperationException.class, ResourceStillReferencedException.class, SearchTermSearchQueryParseException.class, IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public final Mono<ResponseEntity<HttpErrorResponseDTO>> handleGenericBadRequestException(Exception exception, ServerWebExchange exchange) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // For this exception, raise this HTTP Status.
            HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
            LOGGER.info("Exception {} caught: {}. Root cause: {}", exception.getClass(), getErrorMessage(tm, exception), (exception.getCause() != null ? exception.getCause().getMessage() : ""));
            LOGGER.debug(HttpStatus.BAD_REQUEST.getReasonPhrase(), exception);

            // Create the request
            HttpErrorResponseDTO errorResponse = HttpErrorResponseBuilder.defaultErrorResponse(
                httpStatus.value(), 
                httpStatus.getReasonPhrase(), 
                exchange.getRequest().getPath().toString(),
                TracingContextUtility.asMap(tracer.currentSpan().context()));
            errorResponse.setMessage(getErrorMessage(tm, exception));
            return Mono.just(new ResponseEntity<>(errorResponse, httpStatus));
        });
    }

    /**
     * Method that handles thrown {@link ForbiddenException}.
     * @param exception the exception that occurred.
     * @param exchange the exchange.
     * @return the error response.
     */
    @ExceptionHandler({UnauthorizedException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public final Mono<ResponseEntity<HttpErrorResponseDTO>> handleUnauthorizedException(UnauthorizedException exception, ServerWebExchange exchange) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // For this exception, raise this HTTP Status.
            HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;
            LOGGER.info("Exception {} caught: {}. Root cause: {}", exception.getClass(), getErrorMessage(tm, exception), (exception.getCause() != null ? exception.getCause().getMessage() : null));
            LOGGER.debug(httpStatus.getReasonPhrase(), exception);

            // Create the request
            HttpErrorResponseDTO errorResponse = HttpErrorResponseBuilder.defaultErrorResponse(
                httpStatus.value(), 
                httpStatus.getReasonPhrase(), 
                exchange.getRequest().getPath().toString(),
                TracingContextUtility.asMap(tracer.currentSpan().context()));
            errorResponse.setMessage(getErrorMessage(tm, exception));
            return Mono.just(new ResponseEntity<>(errorResponse, httpStatus));
        });
    }

    /**
     * Method that handles thrown {@link ForbiddenException}.
     * @param exception the exception that occurred.
     * @param exchange the exchange.
     * @return the error response.
     */
    @ExceptionHandler({ForbiddenException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public final Mono<ResponseEntity<HttpErrorResponseDTO>> handleForbiddenException(ForbiddenException exception, ServerWebExchange exchange) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // For this exception, raise this HTTP Status.
            HttpStatus httpStatus = HttpStatus.FORBIDDEN;
            LOGGER.info("Exception {} caught: {}. Root cause: {}", exception.getClass(), getErrorMessage(tm, exception), (exception.getCause() != null ? exception.getCause().getMessage() : null));
            LOGGER.debug(HttpStatus.FORBIDDEN.getReasonPhrase(), exception);

            // Create the request
            HttpErrorResponseDTO errorResponse = HttpErrorResponseBuilder.defaultErrorResponse(
                httpStatus.value(), 
                httpStatus.getReasonPhrase(), 
                exchange.getRequest().getPath().toString(),
                TracingContextUtility.asMap(tracer.currentSpan().context()));
            errorResponse.setMessage(getErrorMessage(tm, exception));
            return Mono.just(new ResponseEntity<>(errorResponse, httpStatus));
        });
    }

    /**
     * Method that handles thrown {@link ResourceNotFoundException}.
     * @param exception the exception that occurred.
     * @param exchange the exchange.
     * @return the error response.
     */
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public final Mono<ResponseEntity<HttpErrorResponseDTO>> handleNotFoundException(NotFoundException exception, ServerWebExchange exchange) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // For this exception, raise this HTTP Status.
            HttpStatus httpStatus = HttpStatus.NOT_FOUND;
            LOGGER.info("Exception {} caught: {}. Root cause: {}", exception.getClass(), getErrorMessage(tm, exception), (exception.getCause() != null ? exception.getCause().getMessage() : null));
            LOGGER.debug(HttpStatus.NOT_FOUND.getReasonPhrase(), exception);

            // Create the request
            HttpErrorResponseDTO errorResponse = HttpErrorResponseBuilder.defaultErrorResponse(
                httpStatus.value(), 
                httpStatus.getReasonPhrase(), 
                exchange.getRequest().getPath().toString(),
                TracingContextUtility.asMap(tracer.currentSpan().context()));
            errorResponse.setMessage(getErrorMessage(tm, exception));
            return Mono.just(new ResponseEntity<>(errorResponse, httpStatus));
        });
    }

    /**
     * Method that handles thrown {@link HttpRequestMethodNotSupportedException}.
     * @param exception the exception that occurred.
     * @param exchange the exchange.
     * @return the error response.
     */
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public final Mono<ResponseEntity<HttpErrorResponseDTO>> handlMethodNotAllowedException(Exception exception, ServerWebExchange exchange) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // For this exception, raise this HTTP Status.
            HttpStatus httpStatus = HttpStatus.METHOD_NOT_ALLOWED;
            LOGGER.info("Exception {} caught: {}. Root cause: {}", exception.getClass(), getErrorMessage(tm, exception), (exception.getCause() != null ? exception.getCause().getMessage() : ""));
            LOGGER.debug(HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase(), exception);

            // Create the request
            HttpErrorResponseDTO errorResponse = HttpErrorResponseBuilder.defaultErrorResponse(
                httpStatus.value(), 
                httpStatus.getReasonPhrase(), 
                exchange.getRequest().getPath().toString(),
                TracingContextUtility.asMap(tracer.currentSpan().context()));
            return Mono.just(new ResponseEntity<>(errorResponse, httpStatus));
        });
    }

    /**
     * Method that handles thrown {@link ConflictException}.
     * @param exception the exception that occurred.
     * @param exchange the exchange.
     * @return the error response.
     */
    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public final Mono<ResponseEntity<HttpErrorResponseDTO>> handleConflictException(ConflictException exception, ServerWebExchange exchange) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // For this exception, raise this HTTP Status.
            HttpStatus httpStatus = HttpStatus.CONFLICT;
            LOGGER.info("Exception {} caught: {}. Root cause: {}", exception.getClass(), getErrorMessage(tm, exception), (exception.getCause() != null ? exception.getCause().getMessage() : null));
            LOGGER.debug(HttpStatus.CONFLICT.getReasonPhrase(), exception);

            // Create the request
            HttpErrorResponseDTO errorResponse = HttpErrorResponseBuilder.defaultErrorResponse(
                httpStatus.value(), 
                httpStatus.getReasonPhrase(), 
                exchange.getRequest().getPath().toString(),
                TracingContextUtility.asMap(tracer.currentSpan().context()));
            errorResponse.setMessage(getErrorMessage(tm, exception));
            return Mono.just(new ResponseEntity<>(errorResponse, httpStatus));
        });
    }

    /**
     * Method that handles thrown {@link HttpRequestMethodNotSupportedException}.
     * @param exception the exception that occurred.
     * @param exchange the exchange.
     * @return the error response.
     */
    @ExceptionHandler({HttpMediaTypeNotSupportedException.class})
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public final Mono<ResponseEntity<HttpErrorResponseDTO>> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException exception, ServerWebExchange exchange) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // For this exception, raise this HTTP Status.
            HttpStatus httpStatus = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
            LOGGER.info("Exception {} caught: {}. Root cause: {}", exception.getClass(), getErrorMessage(tm, exception), (exception.getCause() != null ? exception.getCause().getMessage() : ""));
            LOGGER.debug(HttpStatus.UNSUPPORTED_MEDIA_TYPE.getReasonPhrase(), exception);

            // Create the request
            HttpErrorResponseDTO errorResponse = HttpErrorResponseBuilder.defaultErrorResponse(
                httpStatus.value(), 
                httpStatus.getReasonPhrase(), 
                exchange.getRequest().getPath().toString(),
                TracingContextUtility.asMap(tracer.currentSpan().context()));
            return Mono.just(new ResponseEntity<>(errorResponse, httpStatus));
        });
    }

    /**
     * Method that handles thrown {@link RemoteSystemClientErrorException} and {@link InternalServerErrorException}.
     * @param exception the exception that occurred.
     * @param exchange the exchange.
     * @return the error response.
     */
    @ExceptionHandler({RemoteSystemClientErrorException.class, InternalServerErrorException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public final Mono<ResponseEntity<HttpErrorResponseDTO>> handleRemoteSystemClientErrorException(Exception exception, ServerWebExchange exchange) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // For this exception, raise this HTTP Status.
            HttpStatus internalServerError = HttpStatus.INTERNAL_SERVER_ERROR;
            LOGGER.error(getErrorMessage(tm, exception), exception);

            // Create the request
            HttpErrorResponseDTO status = HttpErrorResponseBuilder.defaultErrorResponse(
                internalServerError.value(), 
                internalServerError.getReasonPhrase(), 
                exchange.getRequest().getPath().toString(),
                TracingContextUtility.asMap(tracer.currentSpan().context()));
            return Mono.just(new ResponseEntity<>(status, internalServerError));
        });
    }

    /**
     * Method that handles thrown {@link NotImplementedException}.
     * @param exception the exception that occurred.
     * @param exchange the exchange.
     * @return the error response.
     */
    @ExceptionHandler(NotImplementedException.class)
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public final Mono<ResponseEntity<HttpErrorResponseDTO>> handleNotImplemented(NotImplementedException exception, ServerWebExchange exchange) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // For this exception, raise this HTTP Status.
            HttpStatus notImplementedError = HttpStatus.NOT_IMPLEMENTED;
            LOGGER.error(getErrorMessage(tm, exception), exception);

            // Create the request
            HttpErrorResponseDTO status = HttpErrorResponseBuilder.defaultErrorResponse(
                notImplementedError.value(), 
                notImplementedError.getReasonPhrase(), 
                exchange.getRequest().getPath().toString(),
                TracingContextUtility.asMap(tracer.currentSpan().context()));
            return Mono.just(new ResponseEntity<>(status, notImplementedError));
        });
    }

    /**
     * Method that handles thrown {@link RemoteSystemServerErrorException}.
     * @param exception the exception that occurred.
     * @param exchange the exchange.
     * @return the error response.
     */
    @ExceptionHandler(RemoteSystemServerErrorException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public final Mono<ResponseEntity<HttpErrorResponseDTO>> handleRemoteSystemFailure(RemoteSystemServerErrorException exception, ServerWebExchange exchange) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // For this exception, raise this HTTP Status.
            HttpStatus badGatewayError = HttpStatus.BAD_GATEWAY;
            LOGGER.error(getErrorMessage(tm, exception), exception);

            // Create the request
            HttpErrorResponseDTO status = HttpErrorResponseBuilder.defaultErrorResponse(
                badGatewayError.value(), 
                badGatewayError.getReasonPhrase(), 
                exchange.getRequest().getPath().toString(),
                TracingContextUtility.asMap(tracer.currentSpan().context()));
            return Mono.just(new ResponseEntity<>(status, badGatewayError));
        });
    }

    /**
     * Method that handles thrown {@link RemoteSystemServiceUnavailableException}.
     * @param exception the exception that occurred.
     * @param exchange the exchange.
     * @return the error response.
     */
    @ExceptionHandler(RemoteSystemServiceUnavailableException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public final Mono<ResponseEntity<HttpErrorResponseDTO>> handleRemoteSystemAccessException(RemoteSystemServiceUnavailableException exception, ServerWebExchange exchange) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // For this exception, raise this HTTP Status.
            HttpStatus serviceUnavailableStatus = HttpStatus.BAD_GATEWAY;
            LOGGER.error(getErrorMessage(tm, exception), exception);

            // Create the request
            HttpErrorResponseDTO status = HttpErrorResponseBuilder.defaultErrorResponse(
                serviceUnavailableStatus.value(), 
                serviceUnavailableStatus.getReasonPhrase(), 
                exchange.getRequest().getPath().toString(),
                TracingContextUtility.asMap(tracer.currentSpan().context()));
            return Mono.just(new ResponseEntity<>(status, serviceUnavailableStatus));
        });
    }

    /**
     * Method that handles thrown {@link ServiceUnavailableException}.
     * @param exception the exception that occurred.
     * @param exchange the exchange.
     * @return the error response.
     */
    @ExceptionHandler(ServiceUnavailableException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public final Mono<ResponseEntity<HttpErrorResponseDTO>> handleNotImplemented(ServiceUnavailableException exception, ServerWebExchange exchange) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // For this exception, raise this HTTP Status.
            HttpStatus serviceUnavailableError = HttpStatus.SERVICE_UNAVAILABLE;
            LOGGER.error(getErrorMessage(tm, exception), exception);

            // Create the request
            HttpErrorResponseDTO status = HttpErrorResponseBuilder.defaultErrorResponse(
                serviceUnavailableError.value(), 
                serviceUnavailableError.getReasonPhrase(), 
                exchange.getRequest().getPath().toString(),
                TracingContextUtility.asMap(tracer.currentSpan().context()));
            return Mono.just(new ResponseEntity<>(status, serviceUnavailableError));
        });
    }

    /**
     * Method that handles thrown {@link RemoteSystemGatewayTimeoutException}.
     * @param exception the exception that occurred.
     * @param exchange the exchange.
     * @return the error response.
     */
    @ExceptionHandler(RemoteSystemGatewayTimeoutException.class)
    @ResponseStatus(HttpStatus.GATEWAY_TIMEOUT)
    public final Mono<ResponseEntity<HttpErrorResponseDTO>> handleRemoteSystemTimeoutException(RemoteSystemGatewayTimeoutException exception, ServerWebExchange exchange) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // For this exception, raise this HTTP Status.
            HttpStatus gatewayTimeoutError = HttpStatus.GATEWAY_TIMEOUT;
            LOGGER.error(getErrorMessage(tm, exception), exception);

            // Create the request
            HttpErrorResponseDTO status = HttpErrorResponseBuilder.defaultErrorResponse(
                gatewayTimeoutError.value(), 
                gatewayTimeoutError.getReasonPhrase(), 
                exchange.getRequest().getPath().toString(),
                TracingContextUtility.asMap(tracer.currentSpan().context()));
            return Mono.just(new ResponseEntity<>(status, gatewayTimeoutError));
        });
    }

    /**
     * Translate the exception message.
     * @param tm the trademark.
     * @param exception the exception message.
     * @return the message localized.
     */
    private String getErrorMessage(String tm, Exception exception) {
        if (exception instanceof BaseException e) {
            return getErrorMessage(tm, e.getMessage(), e.getMessageArgs());
        } else {
            return getErrorMessage(tm, exception.getMessage());
        }
    }

    /**
     * Translate the message if it's in curly brackets.
     * @param tm the trademark.
     * @param message the message.
     * @return the message translated.
     */
    private String getErrorMessage(String tm, String message, Object... args) {
        if (message.startsWith("{") && message.endsWith("}")) {
            String code = message.substring(1, message.length() - 1);
            return messageSourceService.getMessage(tm, code, args, code, LocaleContextHolder.getLocale());
        } else {
            return message;
        }
    }
}
