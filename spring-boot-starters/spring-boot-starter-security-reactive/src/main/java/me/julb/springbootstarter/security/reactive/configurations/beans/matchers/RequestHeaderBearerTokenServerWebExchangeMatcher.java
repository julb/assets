package me.julb.springbootstarter.security.reactive.configurations.beans.matchers;

import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.web.server.ServerWebExchange;

import me.julb.library.utility.http.HttpHeaderUtility;

import reactor.core.publisher.Mono;

/**
 * A bearer token request matcher for an header.
 * <br>
 * @author Julb.
 */
public class RequestHeaderBearerTokenServerWebExchangeMatcher implements ServerWebExchangeMatcher {

    /**
     * The headerName attribute.
     */
    private String headerName;

    /**
     * Default constructor.
     * @param headerName the header name.
     */
    public RequestHeaderBearerTokenServerWebExchangeMatcher(String headerName) {
        this.headerName = headerName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<MatchResult> matches(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(headerName))
            .flatMap(token -> HttpHeaderUtility.isBearerToken(token) ? MatchResult.match() : MatchResult.notMatch())
            .switchIfEmpty(MatchResult.notMatch());
    }
}