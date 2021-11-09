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

package me.julb.springbootstarter.core.configs;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.lang.Nullable;

import me.julb.library.utility.constants.Strings;
import me.julb.library.utility.exceptions.InternalServerErrorException;
import me.julb.springbootstarter.core.context.TrademarkContextHolder;

/**
 * The config source service.
 * <br>
 * @author Julb.
 */
public class ConfigSourceService implements ApplicationListener<ContextRefreshedEvent> {

    /**
     * The SENSITIVE_KEY_PREFIX attribute.
     */
    private static final String SENSITIVE_VALUE_PREFIX = "{secure}";

    /**
     * The SENSITIVE_REDACTED_VALUE attribute.
     */
    private static final String SENSITIVE_VALUE_REDACTED = "$$REDACTED$$";

    /**
     * The config source properties.
     */
    @Value("classpath:tm.properties")
    private Resource configSourcePropertiesResource;

    /**
     * The conversion service.
     */
    @Autowired
    private ConversionService mvcConversionService;

    /**
     * The config source properties.
     */
    private Properties configSourceProperties;

    // ------------------------------------------ Init methods.

    /**
     * {@inheritDoc}
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            this.configSourceProperties = PropertiesLoaderUtils.loadProperties(configSourcePropertiesResource);
        } catch (IOException e) {
            throw new InternalServerErrorException(e);
        }
    }

    // ------------------------------------------ Read methods.

    /**
     * Try to resolve the config value.
     * @param code the message code to lookup.
     * @return the config value if found.
     */
    public String getProperty(String code) {
        return getTypedProperty(code, String.class);
    }

    /**
     * Try to resolve the configuration value.
     * @param code the message code to lookup.
     * @param args the arguments to format the output.
     * @return the configuration value if found.
     */
    public String getProperty(String code, Object[] args) {
        String property = getProperty(code);
        if (property != null) {
            return MessageFormat.format(property, args);
        } else {
            return null;
        }
    }

    /**
     * Try to resolve the configuration value.
     * @param code the message code to lookup.
     * @param targetType the target type.
     * @param <T> the type of the target type.
     * @return the configuration value if found.
     */
    public <T> T getTypedProperty(String code, Class<T> targetType) {
        String tm = TrademarkContextHolder.getTrademark();

        // Get property value.
        String tmPropertyValue = configSourceProperties.getProperty(StringUtils.join(Strings.LEFT_BRACKET, tm, Strings.RIGHT_BRACKET, code));
        if (tmPropertyValue == null) {
            tmPropertyValue = configSourceProperties.getProperty(code);
        }

        // Get raw value if sensitive.
        if (isValueSensitive(tmPropertyValue)) {
            tmPropertyValue = getRawValue(tmPropertyValue);
        }

        // If property value provided, convert it.
        if (tmPropertyValue != null) {
            return mvcConversionService.convert(tmPropertyValue, targetType);
        } else {
            return null;
        }
    }

    /**
     * Returns all properties, starting with given prefix if provided.
     * @param prefix the prefix to filter on, or <code>null</code> if no filter is needed.
     * @param redactSensitive <code>true</code> to redact sensitive value, <code>false</code> otherwise.
     * @return the properties.
     */
    public Map<String, String> findAll(@Nullable String prefix, Boolean redactSensitive) {
        // Get trademark.
        String tm = TrademarkContextHolder.getTrademark();

        //@formatter:off
        Map<String, String> defaultProperties = configSourceProperties.entrySet().stream()
            .filter((e) -> {
                // Get property key.
                String propertyKey = (String) e.getKey();
                return !StringUtils.startsWith(propertyKey, Strings.LEFT_BRACKET)
                    && (StringUtils.isBlank(prefix) || StringUtils.startsWith((String) e.getKey(), prefix));
            })
            .map((e) -> {
                // Transform key and value into String keypairs.
                String key = e.getKey().toString().toLowerCase();
                String value = e.getValue().toString();
                
                // If redact sensitive and sensitive, redact value.
                if (isValueSensitive(value)) {
                    if(redactSensitive) {
                        value = SENSITIVE_VALUE_REDACTED;
                    } else {
                        value = getRawValue(value);
                    }
                }
                
                // Return immutable pairs.
                return new ImmutablePair<String, String>(key, value);
            })
            .collect(
                Collectors.toMap(
                     e -> e.getKey(),
                     e -> e.getValue()
                )
            );
        //@formatter:on

        // Determine tm properties prefix.
        String tmPropertiesPrefix = StringUtils.join(Strings.LEFT_BRACKET, tm, Strings.RIGHT_BRACKET);

        // The lookup should include the given prefix.
        String tmPropertiesPrefixFiltering = StringUtils.join(tmPropertiesPrefix, StringUtils.defaultIfBlank(prefix, Strings.EMPTY));

        //@formatter:off
        Map<String, String> tmProperties = configSourceProperties.entrySet().stream()
            .filter((e) -> {
                // Get property key.
                String propertyKey = (String) e.getKey();
                
                // If property starts with [trademark]prefix, keep it.
                return StringUtils.startsWith(propertyKey, tmPropertiesPrefixFiltering);
            })
            .map((e) -> {
                // Transform key and value into String keypairs.
                String key = StringUtils.removeStart(e.getKey().toString().toLowerCase(), tmPropertiesPrefix);
                String value = e.getValue().toString();
                
                // If redact sensitive and sensitive, redact value.
                if (isValueSensitive(value)) {
                    if(redactSensitive) {
                        value = SENSITIVE_VALUE_REDACTED;
                    } else {
                        value = getRawValue(value);
                    }
                }
                
                // Return immutable pairs.
                return new ImmutablePair<String, String>(key, value);
            })
            .collect(
                Collectors.toMap(
                     e -> e.getKey(),
                     e -> e.getValue()
                )
            );
        //@formatter:on

        Map<String, String> finalProperties = new TreeMap<>();
        finalProperties.putAll(defaultProperties);
        finalProperties.putAll(tmProperties);
        return finalProperties;
    }

    // ------------------------------------------ Write methods.

    // ------------------------------------------ Utility methods.

    /**
     * Returns <code>true</code> if the value is sensitive, <code>false</code> otherwise.
     * @param propertyValue the property value.
     * @return <code>true</code> if the value is sensitive, <code>false</code> otherwise.
     */
    protected Boolean isValueSensitive(@Nullable String propertyValue) {
        return propertyValue != null && propertyValue.startsWith(SENSITIVE_VALUE_PREFIX);
    }

    /**
     * Returns the raw value for the given sensitive property value.
     * @param sensitivePropertyValue the sensitive property value.
     * @return the raw value for the given sensitive property value.
     */
    protected String getRawValue(@Nullable String sensitivePropertyValue) {
        return StringUtils.removeStart(sensitivePropertyValue, SENSITIVE_VALUE_PREFIX);
    }
}
