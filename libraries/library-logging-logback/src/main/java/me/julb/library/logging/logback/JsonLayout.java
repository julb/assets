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
package me.julb.library.logging.logback;

/**
 */
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * The custom JSON layout.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
public class JsonLayout extends ch.qos.logback.contrib.json.classic.JsonLayout {

    /**
     * A message format to wrap message and exception.
     */
    private static final String MESSAGE_WITH_EXCEPTION_FORMAT = "{0}. Error is: {1}";

    /**
     * The IRN attribute.
     */
    private static final String LOG_ATTR_IRN = "irn";

    /**
     * The time attribute.
     */
    private static final String LOG_ATTR_TIME = "time";

    /**
     * The request ID attribute.
     */
    private static final String LOG_ATTR_X_REQUEST_ID = "x-request-id";

    //@formatter:off
     /**
     * The irn attribute.
     * -- GETTER --
     * Getter for {@link #irn} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #irn} property.
     * @param irn the value to set.
     */
     //@formatter:on
    private String irn;

    //@formatter:off
     /**
     * The maximumThrowableStackTraceSize attribute.
     * -- GETTER --
     * Getter for {@link #maximumThrowableStackTraceSize} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #maximumThrowableStackTraceSize} property.
     * @param maximumThrowableStackTraceSize the value to set.
     */
     //@formatter:on
    private Integer maximumThrowableStackTraceSize;

    /**
     * Constructor.
     */
    public JsonLayout() {
        super();

        // Configure throwable converter.
        ThrowableProxyConverter converter = new ThrowableProxyConverter();
        converter.setOptionList(Arrays.asList(String.valueOf(this.maximumThrowableStackTraceSize)));
        this.setThrowableProxyConverter(converter);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    protected void addCustomDataToJsonMap(Map<String, Object> map, ILoggingEvent event) {
        // Flatten MDC
        if (map.containsKey(MDC_ATTR_NAME)) {
            Map<String, String> object = (Map) map.get(MDC_ATTR_NAME);
            map.putAll(object);
            map.remove(MDC_ATTR_NAME);
        }

        // Add irn
        map.put(LOG_ATTR_IRN, irn);

        // Rename time field
        if (map.containsKey(TIMESTAMP_ATTR_NAME)) {
            map.put(LOG_ATTR_TIME, map.get(TIMESTAMP_ATTR_NAME));
            map.remove(TIMESTAMP_ATTR_NAME);
        }

        // Lowercase level.
        map.put(LEVEL_ATTR_NAME, ((String) map.get(LEVEL_ATTR_NAME)).toLowerCase());

        // Add default tracking ID.
        if (!map.containsKey(LOG_ATTR_X_REQUEST_ID)) {
            map.put(LOG_ATTR_X_REQUEST_ID, "");
        }

        if (map.containsKey(EXCEPTION_ATTR_NAME)) {
            String exception = (String) map.get(EXCEPTION_ATTR_NAME);

            String formattedMessage = (String) map.get(FORMATTED_MESSAGE_ATTR_NAME);
            if (formattedMessage != null) {
                map.put(FORMATTED_MESSAGE_ATTR_NAME, MessageFormat.format(MESSAGE_WITH_EXCEPTION_FORMAT, formattedMessage, exception));
            } else {
                map.put(FORMATTED_MESSAGE_ATTR_NAME, exception);
            }
        }
    }
}
