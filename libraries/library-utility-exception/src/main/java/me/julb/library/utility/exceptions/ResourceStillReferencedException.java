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
package me.julb.library.utility.exceptions;

import lombok.Getter;

/**
 * This exception is thrown in case a resource is still referenced by another resources.
 * <P>
 * The resource is identified by a class and an identifier, both exposed.
 * <P>
 * @author Julb.
 */
@Getter
public class ResourceStillReferencedException extends BadRequestException {

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
     * The id attribute.
     * -- GETTER --
     * Getter for {@link #id} property.
     * @return the value.
     */
     //@formatter:on
    private String id;

    //@formatter:off
     /**
     * The referringResourceClass attribute.
     * -- GETTER --
     * Getter for {@link #referringResourceClass} property.
     * @return the value.
     */
     //@formatter:on
    private Class<?> referringResourceClass;

    /**
     * Constructor.
     * @param resourceClass the resource class.
     * @param id the resource id.
     * @param referringResourceClass the refering resource class.
     */
    public ResourceStillReferencedException(Class<?> resourceClass, String id, Class<?> referringResourceClass) {
        super();
        this.resourceClass = resourceClass;
        this.id = id;
        this.referringResourceClass = referringResourceClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        return String.format("Resource is still referenced by %s: [%s#%s]", this.referringResourceClass.getSimpleName(), this.resourceClass.getSimpleName(), this.id);
    }
}
