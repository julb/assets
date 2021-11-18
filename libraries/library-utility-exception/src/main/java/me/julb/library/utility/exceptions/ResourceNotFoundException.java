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
package me.julb.library.utility.exceptions;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

/**
 * This exception is thrown in case a resource is not found.
 * <P>
 * The resource is identified by a class and an identifier, both exposed.
 * <br>
 * @author Julb.
 */
@Getter
public class ResourceNotFoundException extends NotFoundException {

    //@formatter:off
     /**
     * The resourceClass attribute.
     * -- GETTER --
     * Getter for {@link #resourceClass} property.
     * @return the value.
     */
     //@formatter:on
    private Class<?> resourceClass;

    //@formatter:off
     /**
     * The attributes attribute.
     * -- GETTER --
     * Getter for {@link #attributes} property.
     * @return the value.
     */
     //@formatter:on
    private Map<String, String> attributes = new HashMap<>();

    /**
     * Constructor.
     * @param resourceClass the resource class.
     * @param id the resource identifier.
     */
    public ResourceNotFoundException(Class<?> resourceClass, String id) {
        super();
        this.resourceClass = resourceClass;
        this.attributes.put("id", id);
    }

    /**
     * Constructor.
     * @param resourceClass the resource class.
     * @param businessKeyAttributeName the attribute name.
     * @param businessKeyAttributeValue the attribute value.
     */
    public ResourceNotFoundException(Class<?> resourceClass, String businessKeyAttributeName, String businessKeyAttributeValue) {
        super();
        this.resourceClass = resourceClass;
        this.attributes.put(businessKeyAttributeName, businessKeyAttributeValue);
    }

    /**
     * Constructor.
     * @param resourceClass the resource class.
     * @param businessKeys the business keys.
     */
    public ResourceNotFoundException(Class<?> resourceClass, Map<String, String> businessKeys) {
        super();
        this.resourceClass = resourceClass;
        this.attributes.putAll(businessKeys);
    }

    /**
     * Constructor.
     * @param resourceClass the resource class.
     * @param id the resource identifier.
     * @param e the cause.
     */
    public ResourceNotFoundException(Class<?> resourceClass, String id, Throwable e) {
        super();
        this.resourceClass = resourceClass;
        this.attributes.put("id", id);
    }

    /**
     * Constructor.
     * @param resourceClass the resource class.
     * @param businessKeyAttributeName the attribute name.
     * @param businessKeyAttributeValue the attribute value.
     * @param e the cause.
     */
    public ResourceNotFoundException(Class<?> resourceClass, String businessKeyAttributeName, String businessKeyAttributeValue, Throwable e) {
        super();
        this.resourceClass = resourceClass;
        this.attributes.put(businessKeyAttributeName, businessKeyAttributeValue);
    }

    /**
     * Constructor.
     * @param resourceClass the resource class.
     * @param businessKeys the business keys.
     * @param e the cause.
     */
    public ResourceNotFoundException(Class<?> resourceClass, Map<String, String> businessKeys, Throwable e) {
        super(e);
        this.resourceClass = resourceClass;
        this.attributes.putAll(businessKeys);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> attribute : attributes.entrySet()) {
            sb.append(String.format("%s=%s,", attribute.getKey(), attribute.getValue()));
        }
        return String.format("Resource not found: [%s#{%s}]", this.resourceClass.getSimpleName(), sb.toString());
    }
}
