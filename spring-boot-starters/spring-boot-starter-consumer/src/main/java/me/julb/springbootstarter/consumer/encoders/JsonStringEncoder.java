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

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.lang.reflect.Type;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;

/**
 * A feign encoder for JSON strings.
 * <P>
 * @author Julb.
 */
public class JsonStringEncoder implements Encoder {

    /**
     * The Jackson Object mapper.
     */
    private ObjectMapper objectMapper;

    /**
     * Constructor.
     */
    public JsonStringEncoder() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void encode(Object object, Type bodyType, RequestTemplate template)
        throws EncodeException {
        if (String.class.equals(bodyType)) {
            template.body(object.toString());
        } else {
            try {
                template.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                template.body(this.objectMapper.writeValueAsString(object));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}