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
package io.julb.library.logging.logback;

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
     * The zone attribute.
     */
    private static final String LOG_ATTR_ZONE = "zone";

    /**
     * The environment attribute.
     */
    private static final String LOG_ATTR_ENVIRONMENT = "env";

    /**
     * The API Name attribute.
     */
    private static final String LOG_ATTR_NAME = "name";

    /**
     * The API version attribute.
     */
    private static final String LOG_ATTR_VERSION = "version";

    /**
     * The correlation ID attribute.
     */
    private static final String LOG_ATTR_OPENTRACING_TRACE_ID = "traceId";

    /**
     * The correlation ID attribute.
     */
    private static final String LOG_ATTR_CORRELATION_ID = "correlationId";

    //@formatter:off
     /**
     * The zone attribute.
     * -- GETTER --
     * Getter for {@link #zone} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #zone} property.
     * @param zone the value to set.
     */
     //@formatter:on
    private String zone;

    //@formatter:off
     /**
     * The environment attribute.
     * -- GETTER --
     * Getter for {@link #environment} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #environment} property.
     * @param environment the value to set.
     */
     //@formatter:on
    private String environment;

    //@formatter:off
     /**
     * The name attribute.
     * -- GETTER --
     * Getter for {@link #name} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #name} property.
     * @param name the value to set.
     */
     //@formatter:on
    private String name;

    //@formatter:off
     /**
     * The version attribute.
     * -- GETTER --
     * Getter for {@link #version} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #version} property.
     * @param version the value to set.
     */
     //@formatter:on
    private String version;

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

        // Add common attributes.
        map.put(LOG_ATTR_ENVIRONMENT, environment);
        map.put(LOG_ATTR_ZONE, zone);
        map.put(LOG_ATTR_NAME, name);
        map.put(LOG_ATTR_VERSION, version);

        if (map.containsKey(EXCEPTION_ATTR_NAME)) {
            String exception = (String) map.get(EXCEPTION_ATTR_NAME);

            String formattedMessage = (String) map.get(FORMATTED_MESSAGE_ATTR_NAME);
            if (formattedMessage != null) {
                map.put(FORMATTED_MESSAGE_ATTR_NAME, MessageFormat.format(MESSAGE_WITH_EXCEPTION_FORMAT, formattedMessage, exception));
            } else {
                map.put(FORMATTED_MESSAGE_ATTR_NAME, exception);
            }
        }

        if (map.containsKey(LOG_ATTR_OPENTRACING_TRACE_ID)) {
            map.put(LOG_ATTR_CORRELATION_ID, map.get(LOG_ATTR_OPENTRACING_TRACE_ID));
        } else {
            map.put(LOG_ATTR_CORRELATION_ID, "");
        }
    }

}
