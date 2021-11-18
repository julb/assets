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
package me.julb.springbootstarter.consumer.decoders;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import org.springframework.cloud.openfeign.support.PageJacksonModule;
import org.springframework.cloud.openfeign.support.SortJacksonModule;

import feign.Response;
import feign.Util;
import feign.codec.Decoder;

/**
 * A feign decoder for JSON strings.
 * <br>
 * @author Julb.
 */
public class JsonStringDecoder implements Decoder {

    /**
     * The object mapper.
     */
    private ObjectMapper objectMapper;

    /**
     * Constructor.
     */
    public JsonStringDecoder() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.objectMapper.registerModule(new PageJacksonModule());
        this.objectMapper.registerModule(new SortJacksonModule());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object decode(Response response, Type type)
        throws IOException {
        Response.Body body = response.body();
        if (body == null) {
            return null;
        }
        if (String.class.equals(type)) {
            return Util.toString(body.asReader(Charset.defaultCharset()));
        } else {
            try {
                return this.objectMapper.readValue(body.asReader(Charset.defaultCharset()), new JacksonCustomTypeReference(type));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}