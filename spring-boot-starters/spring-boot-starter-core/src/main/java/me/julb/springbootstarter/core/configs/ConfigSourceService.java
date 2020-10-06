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

package me.julb.springbootstarter.core.configs;

import java.text.MessageFormat;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import me.julb.library.utility.constants.Strings;
import me.julb.springbootstarter.core.context.TrademarkContextHolder;

/**
 * The config source service.
 * <P>
 * @author Julb.
 */
public class ConfigSourceService {

    /**
     * The Spring environment.
     */
    @Autowired
    private Environment environment;

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

        T tmProperty = environment.getProperty(StringUtils.join(tm, Strings.DOT, code), targetType);
        if (tmProperty == null) {
            return environment.getProperty(code, targetType);
        } else {
            return tmProperty;
        }
    }
}
