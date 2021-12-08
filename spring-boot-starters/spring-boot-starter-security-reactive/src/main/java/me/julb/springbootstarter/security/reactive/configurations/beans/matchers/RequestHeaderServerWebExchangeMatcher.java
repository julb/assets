package me.julb.springbootstarter.security.reactive.configurations.beans.matchers;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * A request matcher for an header.
 * <br>
 * @author Julb.
 */
public class RequestHeaderServerWebExchangeMatcher implements ServerWebExchangeMatcher {

    /**
     * The headerName attribute.
     */
    private String headerName;

    /**
     * Default constructor.
     * @param headerName the header name.
     */
    public RequestHeaderServerWebExchangeMatcher(String headerName) {
        super();
        this.headerName = headerName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<MatchResult> matches(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange.getRequest().getHeaders().get(headerName))
            .flatMap(headerValues -> CollectionUtils.isNotEmpty(headerValues) ? MatchResult.match() : MatchResult.notMatch())
            .switchIfEmpty(MatchResult.notMatch());
    }

    /**
     * Getter for property headerName.
     * @return Value of property headerName.
     */
    protected String getHeaderName() {
        return headerName;
    }
}