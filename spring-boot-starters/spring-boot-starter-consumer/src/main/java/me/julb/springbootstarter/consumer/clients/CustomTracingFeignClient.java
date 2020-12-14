/*
 * Copyright 2013-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.julb.springbootstarter.consumer.clients;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brave.Span;
import brave.Tracer;
import brave.http.HttpClientHandler;
import brave.http.HttpTracing;
import brave.propagation.Propagation;
import brave.propagation.TraceContext;
import feign.Client;
import feign.Request;
import feign.Request.HttpMethod;
import feign.Response;

/**
 * This is a custom tracing feign client to be used when creating.
 * <P>
 * This is a copy from org.springframework.cloud.sleuth.instrument.web.client.feign.TracingFeignClient.
 * <P>
 * @author Julb.
 */
@SuppressWarnings("deprecation")
public final class CustomTracingFeignClient implements Client {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomTracingFeignClient.class);

    static final Propagation.Setter<Map<String, Collection<String>>, String> SETTER = new Propagation.Setter<Map<String, Collection<String>>, String>() {
        @Override
        public void put(Map<String, Collection<String>> carrier, String key, String value) {
            if (!carrier.containsKey(key)) {
                carrier.put(key, Collections.singletonList(value));
                LOGGER.debug("Added key [" + key + "] and header value [" + value + "]");
            } else {
                LOGGER.debug("Key [" + key + "] already there in the headers");
            }
        }

        @Override
        public String toString() {
            return "Map::set";
        }
    };

    final Tracer tracer;

    final Client delegate;

    final HttpClientHandler<Request, Response> handler;

    final TraceContext.Injector<Map<String, Collection<String>>> injector;

    CustomTracingFeignClient(HttpTracing httpTracing, Client delegate) {
        this.tracer = httpTracing.tracing().tracer();
        this.handler = HttpClientHandler.create(httpTracing, new HttpAdapter());
        this.injector = httpTracing.tracing().propagation().injector(SETTER);
        this.delegate = delegate;
    }

    public static Client create(HttpTracing httpTracing, Client delegate) {
        return new CustomTracingFeignClient(httpTracing, delegate);
    }

    @Override
    public Response execute(Request request, Request.Options options)
        throws IOException {
        Map<String, Collection<String>> headers = new HashMap<>(request.headers());
        Span span = handleSend(headers, request, null);
        LOGGER.debug("Handled send of " + span);
        Response response = null;
        Throwable error = null;
        try (Tracer.SpanInScope ws = this.tracer.withSpanInScope(span)) {
            response = this.delegate.execute(modifiedRequest(request, headers), options);
            return response;
        } catch (IOException | RuntimeException | Error e) {
            error = e;
            throw e;
        } finally {
            handleReceive(span, response, error);
            LOGGER.debug("Handled receive of " + span);
        }
    }

    Span handleSend(Map<String, Collection<String>> headers, Request request, Span clientSpan) {
        if (clientSpan != null) {
            return this.handler.handleSend(this.injector, headers, request, clientSpan);
        }
        return this.handler.handleSend(this.injector, headers, request);
    }

    void handleReceive(Span span, Response response, Throwable error) {
        this.handler.handleReceive(response, error, span);
    }

    private Request modifiedRequest(Request request, Map<String, Collection<String>> headers) {
        HttpMethod method = request.httpMethod();
        String url = request.url();
        byte[] body = request.body();
        Charset charset = request.charset();
        return Request.create(method, url, headers, body, charset);
    }

    static final class HttpAdapter extends brave.http.HttpClientAdapter<Request, Response> {

        @Override
        public String method(Request request) {
            return request.httpMethod().name();
        }

        @Override
        public String url(Request request) {
            return request.url();
        }

        @Override
        public String requestHeader(Request request, String name) {
            Collection<String> result = request.headers().get(name);
            return result != null && result.iterator().hasNext() ? result.iterator().next() : null;
        }

        @Override
        public Integer statusCode(Response response) {
            return response.status();
        }

    }

}