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
package me.julb.springbootstarter.security.reactive.configurations.beans.filters;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.AndServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;

import me.julb.springbootstarter.security.configurations.beans.authenticationtokens.CustomInternalApiKeyPreAuthenticatedAuthenticationToken;
import me.julb.springbootstarter.security.reactive.configurations.beans.matchers.RequestHeaderServerWebExchangeMatcher;

import reactor.core.publisher.Mono;

/**
 * The pre-authenticated header authentication filter for internal API keys.
 * <br>
 * @author Julb.
 */
public class AuthenticationByInternalApiKeyAuthenticationWebFilter extends AuthenticationWebFilter {

    /**
     * Constructor.
     * @param reactiveAuthenticationManager the reactive authentication manager.
     * @param apiKeyHeader the API key header.
     */
    public AuthenticationByInternalApiKeyAuthenticationWebFilter(ReactiveAuthenticationManager reactiveAuthenticationManager, String apiKeyHeader) {
        super(reactiveAuthenticationManager);
        this.setRequiresAuthenticationMatcher(new AndServerWebExchangeMatcher(ServerWebExchangeMatchers.pathMatchers("/**"), new RequestHeaderServerWebExchangeMatcher(apiKeyHeader)));
        this.setServerAuthenticationConverter(exchange -> {
            String internalApiKey = exchange.getRequest().getHeaders().getFirst(apiKeyHeader);
            return Mono.just(new CustomInternalApiKeyPreAuthenticatedAuthenticationToken(internalApiKey, "N/A"));
        });
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // TODO Auto-generated method stub
        return super.filter(exchange, chain);
    }
}
