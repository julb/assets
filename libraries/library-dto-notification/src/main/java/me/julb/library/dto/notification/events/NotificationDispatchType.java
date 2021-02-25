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
    MAIL(TemplatingMode.HTML, true),

    /**
     * The SMS notification dispatch type.
     */
    SMS(TemplatingMode.TEXT, false),

    /**
     * The Google chat notification dispatch type.
     */
    GOOGLE_CHAT(TemplatingMode.TEXT, false),

    /**
     * The Web notification dispatch type.
     */
    WEB(TemplatingMode.NONE, false),

    /**
     * The Push notification dispatch type.
     */
    PUSH(TemplatingMode.TEXT, true);

    /**
     * The templating mode.
     */
    private TemplatingMode templatingMode;

    /**
     * Flag indicating if the template expects a subject.
     */
    private boolean hasSubject;

    /**
     * Default constructor.
     * @param templatingMode the templating mode.
     * @param hasSubject <code>true</code> if the type has a subject, <code>false</code> otherwise.
     */
    private NotificationDispatchType(TemplatingMode templatingMode, boolean hasSubject) {
        this.templatingMode = templatingMode;
        this.hasSubject = hasSubject;
    }

    /**
     * Returns the templating mode.
     * @return the templating mode.
     */
    public TemplatingMode templatingMode() {
        return this.templatingMode;
    }

    /**
     * Getter for property hasSubject.
     * @return Value of property hasSubject.
     */
    public boolean hasSubject() {
        return hasSubject;
    }
}
