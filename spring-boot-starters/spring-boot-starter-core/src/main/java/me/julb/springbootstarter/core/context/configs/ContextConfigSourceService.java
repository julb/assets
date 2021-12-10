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

package me.julb.springbootstarter.core.context.configs;

import java.text.MessageFormat;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;

import me.julb.springbootstarter.core.configs.ConfigSourceService;
import me.julb.springbootstarter.core.context.TrademarkContextHolder;

/**
 * The context config source service.
 * <br>
 * This class uses ThreadLocal to extract context and delegates to {@link ConfigSourceService}.
 * @author Julb.
 */
public class ContextConfigSourceService {

    /**
     * The config source properties.
     */
    @Autowired
    private ConfigSourceService delegate;

    // ------------------------------------------ Init methods.

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
        return delegate.getTypedProperty(tm, code, targetType);
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

        return delegate.findAll(tm, prefix, redactSensitive);
    }

    // ------------------------------------------ Write methods.

    // ------------------------------------------ Utility methods.
}
