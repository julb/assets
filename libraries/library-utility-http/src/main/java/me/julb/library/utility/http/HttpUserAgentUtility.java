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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.apache.commons.lang3.StringUtils;

import me.julb.library.dto.http.client.BrowserDTO;
import me.julb.library.dto.http.client.DeviceDTO;
import me.julb.library.dto.http.client.OperatingSystemDTO;
import me.julb.library.utility.constants.Chars;

import ua_parser.Client;
import ua_parser.Parser;

/**
 * The HTTP user agent utility.
 * <br>
 *
 * @author Julb.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HttpUserAgentUtility {

    /**
     * Gets the user browser.
     * @param userAgent the user agent.
     * @return the browser.
     */
    public static BrowserDTO getBrowser(String userAgent) {
        if (StringUtils.isNotBlank(userAgent)) {
            Parser uaParser = new Parser();
            Client client = uaParser.parse(userAgent);
            return new BrowserDTO(client.userAgent.family, client.userAgent.major, StringUtils.join(new String[] {client.userAgent.major, client.userAgent.minor, client.userAgent.patch}, Chars.DOT));
        } else {
            return null;
        }
    }

    /**
     * Gets the operating system.
     * @param userAgent the user agent.
     * @return the operating system.
     */
    public static OperatingSystemDTO getOperatingSystem(String userAgent) {
        if (StringUtils.isNotBlank(userAgent)) {
            Parser uaParser = new Parser();
            Client client = uaParser.parse(userAgent);
            return new OperatingSystemDTO(client.os.family, client.os.major, StringUtils.join(new String[] {client.os.major, client.os.minor, client.os.patch, client.os.patchMinor}, Chars.DOT));
        } else {
            return null;
        }
    }

    /**
     * Gets the device.
     * @param userAgent the user agent.
     * @return the device.
     */
    public static DeviceDTO getDevice(String userAgent) {
        if (StringUtils.isNotBlank(userAgent)) {
            Parser uaParser = new Parser();
            Client client = uaParser.parse(userAgent);
            return new DeviceDTO(client.device.family);
        } else {
            return null;
        }
    }
}
