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

import io.julb.library.dto.http.error.HttpErrorResponseDTO;
import io.julb.library.utility.http.HttpErrorResponseBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

/**
 * A controller advice defined to catch all global exceptions.
 * <P>
 * @author Julb.
 */
@RestController
@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class UncaughtExceptionAdviceController {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UncaughtExceptionAdviceController.class);

    /**
     * Method that handles thrown {@link Exception}.
     * @param exception the exception that occurred.
     * @return the error response.
     */
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<HttpErrorResponseDTO> handleInternalError(Exception exception) {
        // For this exception, raise this HTTP Status.
        HttpStatus internalServerErrorStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        LOGGER.error(exception.getMessage(), exception);

        // Create the request
        HttpErrorResponseDTO status = HttpErrorResponseBuilder.defaultErrorResponse(internalServerErrorStatus.value(), internalServerErrorStatus.getReasonPhrase());
        return new ResponseEntity<>(status, internalServerErrorStatus);
    }
}
