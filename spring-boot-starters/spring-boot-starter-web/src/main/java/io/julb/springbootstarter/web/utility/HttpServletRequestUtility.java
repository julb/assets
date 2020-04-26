/**
 * MIT License
 *
 * Copyright (c) 2017-2019 Julb
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

package io.julb.springbootstarter.web.utility;

import io.julb.library.utility.constants.CustomHttpHeaders;

import javax.servlet.http.HttpServletRequest;

/**
 * The HTTP servlet request utility.
 * <P>
 * @author Julb.
 */
public class HttpServletRequestUtility {
    /**
     * Gets the user IPV4 address.
     * @param httpServletRequest the request.
     * @return the IPV4 address if available.
     */
    public static String getUserIpv4Address(HttpServletRequest httpServletRequest) {
        String ipv4 = httpServletRequest.getHeader(CustomHttpHeaders.X_REAL_IP);
        if (ipv4 == null) {
            ipv4 = httpServletRequest.getHeader(CustomHttpHeaders.X_FORWARDED_FOR);
        }
        if (ipv4 == null) {
            ipv4 = httpServletRequest.getRemoteAddr();
        }
        return ipv4;
    }

    /**
     * Gets the user browser.
     * @param httpServletRequest the request.
     * @return the browser.
     */
    public static String getBrowser(HttpServletRequest httpServletRequest) {
        // TODO Auto-generated method stub
        return "Chrome";
    }

    /**
     * Gets the operating system.
     * @param httpServletRequest the request.
     * @return the operating system.
     */
    public static String getOperatingSystem(HttpServletRequest httpServletRequest) {
        // TODO Auto-generated method stub
        return "Windows";
    }
}
