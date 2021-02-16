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

package me.julb.library.dto.notification.events;

import me.julb.library.utility.enums.TemplatingMode;

/**
 * The notification dispatch type.
 * <P>
 * @author Julb.
 */
public enum NotificationDispatchType {

    /**
     * The MAIL notification dispatch type.
     */
    MAIL(TemplatingMode.HTML),

    /**
     * The SMS notification dispatch type.
     */
    SMS(TemplatingMode.TEXT),

    /**
     * The Google chat notification dispatch type.
     */
    GOOGLE_CHAT(TemplatingMode.TEXT),

    /**
     * The Web notification dispatch type.
     */
    WEB(TemplatingMode.HTML);

    /**
     * The templating mode.
     */
    private TemplatingMode templatingMode;

    /**
     * Default constructor.
     */
    private NotificationDispatchType(TemplatingMode templatingMode) {
        this.templatingMode = templatingMode;
    }

    /**
     * Returns the templating mode.
     * @return the templating mode.
     */
    public TemplatingMode templatingMode() {
        return this.templatingMode;
    }
}
