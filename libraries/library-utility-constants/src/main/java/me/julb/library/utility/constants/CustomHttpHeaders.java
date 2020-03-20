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
package me.julb.library.utility.constants;

/**
 * A class to list custom HTTP headers.
 * <P>
 * @author Julb.
 */
public class CustomHttpHeaders {

    /**
     * The header for getting trademark.
     */
    public static final String X_JULB_TM = "x-julb-tm";

    /**
     * The header for debug purposes.
     */
    public static final String X_JULB_HTTP_TRACE_ENABLED = "x-julb-http-trace-enabled";

    /**
     * The header for real IP address.
     */
    public static final String X_REAL_IP = "x-real-ip";

    /**
     * The header for X-Forwarded-For value.
     */
    public static final String X_FORWARDED_FOR = "x-forwarded-for";

    /**
     * The header for Google ReCaptcha token.
     */
    public static final String X_GOOGLE_RECAPTCHA_TOKEN = "x-google-recaptcha-token";

    /**
     * The header for Google ReCaptcha action.
     */
    public static final String X_GOOGLE_RECAPTCHA_ACTION = "x-google-recaptcha-action";

}
