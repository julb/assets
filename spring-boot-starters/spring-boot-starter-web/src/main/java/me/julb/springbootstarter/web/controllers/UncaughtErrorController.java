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
package me.julb.springbootstarter.web.controllers;

import io.swagger.v3.oas.annotations.Hidden;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.julb.library.dto.http.error.HttpErrorResponseDTO;
import me.julb.library.utility.http.HttpErrorResponseBuilder;

/**
 * Error management called when the Exception advice fails, or for a 404 response for example.
 * <P>
 * @author Julb.
 */
@RestController
@Slf4j
@Hidden
public class UncaughtErrorController implements ErrorController {

    /**
     * Method that handles the error endpoint.
     * @param request the HTTP Servlet request.
     * @param response the HTTP Servlet response.
     * @return the error response.
     */
    @RequestMapping(path = "${server.error.path:/error}")
    public ResponseEntity<HttpErrorResponseDTO> handleError(HttpServletRequest request, HttpServletResponse response) {

        // Log exception
        Exception exception = (Exception) request.getAttribute("javax.servlet.error.exception");
        if (exception != null) {
            LOGGER.error("Uncaught exception", exception);
        }

        // Get the status code from request.
        Integer statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        // Get the HTTP status.
        HttpStatus httpStatus = HttpStatus.valueOf(statusCode);

        // Create the response
        HttpErrorResponseDTO status = HttpErrorResponseBuilder.defaultErrorResponse(httpStatus.value(), httpStatus.getReasonPhrase());
        return new ResponseEntity<>(status, httpStatus);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public String getErrorPath() {
        return null;
    }
}
