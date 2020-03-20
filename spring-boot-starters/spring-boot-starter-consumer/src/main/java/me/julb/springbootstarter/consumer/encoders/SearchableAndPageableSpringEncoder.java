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
package me.julb.springbootstarter.consumer.encoders;

import java.lang.reflect.Type;

import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import me.julb.springbootstarter.consumer.utility.SearchableAndPageable;

/**
 * The searchable Spring encoder.
 * <P>
 * @author Julb.
 */
public class SearchableAndPageableSpringEncoder implements Encoder {

    /**
     * The delegate attribute.
     */
    private final Encoder delegate;

    /**
     * Creates a new PageableSpringEncoder with the given delegate for fallback. If no delegate is provided and this encoder cant handle the request, an EncodeException is thrown.
     * @param delegate The optional delegate.
     */
    /**
     * Default constructor.
     * @param delegate the given delegate for fallback. If no delegate is provided and this encoder cant handle the request, an EncodeException is thrown.
     */
    public SearchableAndPageableSpringEncoder(Encoder delegate) {
        this.delegate = delegate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void encode(Object object, Type bodyType, RequestTemplate template)
        throws EncodeException {

        if (supports(object)) {
            // Get instance
            SearchableAndPageable sap = (SearchableAndPageable) object;

            // Encode pageable
            CustomPageableSpringEncoder pageableEncoder = new CustomPageableSpringEncoder(null);
            pageableEncoder.encode(sap.getPageable(), bodyType, template);

            // Encode searchable
            SearchableSpringEncoder searchableEncoder = new SearchableSpringEncoder(null);
            searchableEncoder.encode(sap.getSearchable(), bodyType, template);
        } else {
            if (delegate != null) {
                delegate.encode(object, bodyType, template);
            } else {
                throw new EncodeException("PageableSpringEncoder does not support the given object " + object.getClass() + " and no delegate was provided for fallback!");
            }
        }
    }

    /**
     * Checks if the given object should be encoded by this encoder or not.
     * @param object the object.
     * @return <code>true</code> if the object should be encoded by this encoder, <code>false</code> otherwise.
     */
    protected boolean supports(Object object) {
        return object instanceof SearchableAndPageable;
    }

}