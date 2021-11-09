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

package me.julb.applications.notificationdispatcher.configurations.beans;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;

import java.util.EnumMap;

import org.springframework.stereotype.Component;

import me.julb.library.dto.notification.events.NotificationDispatchType;

/**
 * The up meter binder.
 * <br>
 * @author Julb.
 */
@Component
public class NotificationDispatcherMeterBinder implements MeterBinder {

    /**
     * The metrics name.
     */
    private static final String NOTIFICATION_PROCESSED_COUNT_METRICS_NAME = "notification_processed_count";

    /**
     * The metrics name.
     */
    private static final String NOTIFICATION_DISPATCHED_COUNT_METRICS_NAME = "notification_dispatched_count";

    /**
     * The notificationDispatchedCounters attribute.
     */
    private EnumMap<NotificationDispatchType, Counter> notificationDispatchedCounters;

    /**
     * The notificationDispatchedCounters attribute.
     */
    private Counter notificationProcessedCounter;

    // ------------------------------------------ Public methods.
    /**
     * Increments the number of processed notifications.
     */
    public void incrementProcessed() {
        notificationProcessedCounter.increment();
    }

    /**
     * Increments the number of dispatched notification of given type.
     * @param type the notification type.
     */
    public void incrementDispatched(NotificationDispatchType type) {
        notificationDispatchedCounters.get(type).increment();
    }

    // ------------------------------------------ Overridden methods.

    /**
     * {@inheritDoc}
     */
    @Override
    public void bindTo(MeterRegistry registry) {
        // Processed
        notificationProcessedCounter = registry.counter(NOTIFICATION_PROCESSED_COUNT_METRICS_NAME);

        // Dispatched
        notificationDispatchedCounters = new EnumMap<>(NotificationDispatchType.class);
        for (NotificationDispatchType type : NotificationDispatchType.values()) {
            notificationDispatchedCounters.put(type, registry.counter(NOTIFICATION_DISPATCHED_COUNT_METRICS_NAME, "type", type.toString()));
        }
    }

}
