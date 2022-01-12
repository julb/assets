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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.AndServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

import me.julb.springbootstarter.security.configurations.beans.authenticationtokens.CustomApiKeyAuthenticationToken;
import me.julb.springbootstarter.security.reactive.configurations.beans.matchers.RequestHeaderServerWebExchangeMatcher;
import me.julb.springbootstarter.security.services.PasswordEncoderService;

import io.micrometer.core.instrument.util.StringUtils;
import reactor.core.publisher.Mono;

/**
 * The pre-authenticated header authentication filter for API key.
 * <br>
 * @author Julb.
 */
public class AuthenticationByApiKeyAuthenticationWebFilter extends AuthenticationWebFilter implements Ordered {

    /**
     * The password encoder service.
     */
    @Autowired
    private PasswordEncoderService passwordEncoderService;

    /**
     * Default constructor.
     * @param reactiveAuthenticationManager the authentication manager.
     * @param apiKeyHeaderName the API key header.
     */
    public AuthenticationByApiKeyAuthenticationWebFilter(ReactiveAuthenticationManager reactiveAuthenticationManager, String apiKeyHeaderName) {
        super(reactiveAuthenticationManager);
        this.setRequiresAuthenticationMatcher(new AndServerWebExchangeMatcher(ServerWebExchangeMatchers.pathMatchers("/**"), new RequestHeaderServerWebExchangeMatcher(apiKeyHeaderName)));
        this.setServerAuthenticationConverter(exchange -> {
            String apiKey = exchange.getRequest().getHeaders().getFirst(apiKeyHeaderName);
            if (StringUtils.isNotBlank(apiKey)) {
                String hashedApiKey = passwordEncoderService.hash(apiKey);
                return Mono.just(new CustomApiKeyAuthenticationToken(apiKey, hashedApiKey));
            } else {
                return Mono.empty();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getOrder() {
        return 20;
    }
}
