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

package me.julb.library.dto.notification.events;

/**
 * The notification kind.
 * <br>
 * @author Julb.
 */
public enum NotificationKind {

    /**
     * Sent when triggering a mail verification.
     */
    TRIGGER_MAIL_VERIFY(NotificationBusinessCategory.ACCOUNT_MANAGEMENT),

    /**
     * Sent when triggering a mobile phone verification.
     */
    TRIGGER_MOBILE_PHONE_VERIFY(NotificationBusinessCategory.ACCOUNT_MANAGEMENT),

    /**
     * Sent when triggering a password reset by mail.
     */
    TRIGGER_PASSWORD_RESET_WITH_MAIL(NotificationBusinessCategory.ACCOUNT_MANAGEMENT),

    /**
     * Sent when triggering a password reset by mobile phone.
     */
    TRIGGER_PASSWORD_RESET_WITH_MOBILE_PHONE(NotificationBusinessCategory.ACCOUNT_MANAGEMENT),

    /**
     * Sent when triggering a pincode reset by mail.
     */
    TRIGGER_PINCODE_RESET_WITH_MAIL(NotificationBusinessCategory.ACCOUNT_MANAGEMENT),

    /**
     * Sent when triggering a pincode reset by mobile phone.
     */
    TRIGGER_PINCODE_RESET_WITH_MOBILE_PHONE(NotificationBusinessCategory.ACCOUNT_MANAGEMENT);

    /**
     * The business category of the notification.
     */
    private NotificationBusinessCategory category;

    /**
     * Default constructor.
     * @param category the category.
     */
    private NotificationKind(NotificationBusinessCategory category) {
        this.category = category;
    }

    /**
     * Getter for property category.
     * @return Value of property category.
     */
    public NotificationBusinessCategory category() {
        return category;
    }
}
