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

package me.julb.springbootstarter.web.reactive.utility;

import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;

import me.julb.library.dto.http.client.BrowserDTO;
import me.julb.library.dto.http.client.DeviceDTO;
import me.julb.library.dto.http.client.OperatingSystemDTO;
import me.julb.library.utility.constants.Chars;
import me.julb.library.utility.constants.CustomHttpHeaders;
import me.julb.library.utility.http.HttpUserAgentUtility;

/**
 * The HTTP servlet request utility.
 * <br>
 * @author Julb.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ServerHttpRequestUtility {
    /**
     * Gets the user IPV4 address.
     * @param serverHttpRequest the request.
     * @return the IPV4 address if available.
     */
    public static String getUserIpAddress(ServerHttpRequest serverHttpRequest) {
        String ipAddress = serverHttpRequest.getHeaders().getFirst(CustomHttpHeaders.X_REAL_IP);
        if (ipAddress == null) {
            List<String> xForwardedForAddresses = serverHttpRequest.getHeaders().get(CustomHttpHeaders.X_FORWARDED_FOR);
            if (CollectionUtils.isNotEmpty(xForwardedForAddresses)) {
                ipAddress = StringUtils.join(xForwardedForAddresses, Chars.COMMA);
            }
        }
        if (ipAddress == null && serverHttpRequest.getRemoteAddress() != null && serverHttpRequest.getRemoteAddress().getAddress() != null) {
            ipAddress = serverHttpRequest.getRemoteAddress().getAddress().getHostAddress();
        }
        return ipAddress;
    }

    /**
     * Gets the user agent.
     * @param serverHttpRequest the request.
     * @return the user agent if available.
     */
    public static String getUserAgent(ServerHttpRequest serverHttpRequest) {
        return serverHttpRequest.getHeaders().getFirst(CustomHttpHeaders.USER_AGENT);
    }

    /**
     * Gets the user browser.
     * @param serverHttpRequest the request.
     * @return the browser.
     */
    public static BrowserDTO getBrowser(ServerHttpRequest serverHttpRequest) {
        String userAgent = getUserAgent(serverHttpRequest);
        return HttpUserAgentUtility.getBrowser(userAgent);
    }

    /**
     * Gets the operating system.
     * @param serverHttpRequest the request.
     * @return the operating system.
     */
    public static OperatingSystemDTO getOperatingSystem(ServerHttpRequest serverHttpRequest) {
        String userAgent = getUserAgent(serverHttpRequest);
        return HttpUserAgentUtility.getOperatingSystem(userAgent);
    }

    /**
     * Gets the device.
     * @param serverHttpRequest the request.
     * @return the device.
     */
    public static DeviceDTO getDevice(ServerHttpRequest serverHttpRequest) {
        String userAgent = getUserAgent(serverHttpRequest);
        return HttpUserAgentUtility.getDevice(userAgent);
    }
}
