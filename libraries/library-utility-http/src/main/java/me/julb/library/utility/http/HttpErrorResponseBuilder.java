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
package me.julb.library.utility.http;

import java.util.Map;

import me.julb.library.dto.http.error.HttpErrorResponseDTO;
import me.julb.library.utility.date.DateUtility;

/**
 * The error response builder.
 * <br>
 * @author Julb.
 */
public final class HttpErrorResponseBuilder {

    /**
     * The default error request.
     * @param code the HTTP code.
     * @param reason the reason.
     * @return a default error response.
     */
    public static HttpErrorResponseDTO defaultErrorResponse(Integer code, String reason) {
        HttpErrorResponseDTO errorResponse = new HttpErrorResponseDTO();
        errorResponse.setDateTime(DateUtility.dateTimeNow());
        errorResponse.setHttpStatus(code);
        errorResponse.setMessage(reason);
        return errorResponse;
    }

    /**
     * The default error request.
     * @param code the HTTP code.
     * @param reason the reason.
     * @param path the path.
     * @param tracingContext the tracing context.
     * @return a default error response.
     */
    public static HttpErrorResponseDTO defaultErrorResponse(Integer code, String reason, String path, Map<String, String> tracingContext) {
        HttpErrorResponseDTO errorResponse = defaultErrorResponse(code, reason);
        errorResponse.setPath(path);
        errorResponse.setTracingContext(tracingContext);
        return errorResponse;
    }
}
