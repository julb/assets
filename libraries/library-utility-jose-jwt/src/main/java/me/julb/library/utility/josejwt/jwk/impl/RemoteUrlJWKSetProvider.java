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
package me.julb.library.utility.josejwt.jwk.impl;

import com.nimbusds.jose.jwk.JWKSet;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.temporal.ChronoUnit;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.julb.library.utility.date.DateUtility;
import me.julb.library.utility.josejwt.exceptions.badrequest.TokenNotParseableJOSEJWTException;
import me.julb.library.utility.josejwt.jwk.IJWKSetProvider;

/**
 * A JWKS provider based on a stringified JSON.
 * <P>
 * @author Julb.
 */
public class RemoteUrlJWKSetProvider implements IJWKSetProvider {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteUrlJWKSetProvider.class);

    /**
     * The URL returning the JSON.
     */
    private String url;

    /**
     * The cache validity in seconds (30mn by default).
     */
    private Integer cacheValidityInSeconds = 1800;

    /**
     * The last JSON string.
     */
    private String lastJSONString;

    /**
     * The not retry before date time.
     */
    private String notRetryBeforeDateTime;

    /**
     * The not retry before internal in seconds.
     */
    private Integer notRetryBeforeIntervalInSeconds = 30;

    /**
     * The expiry date time.
     */
    private String expiryDateTime;

    // ------------------------------------------ Constructors.

    /**
     * Constructor.
     */
    public RemoteUrlJWKSetProvider() {
        super();
    }

    // ------------------------------------------ Overridden methods.

    /**
     * {@inheritDoc}
     */
    @Override
    public JWKSet toJWKSet() {
        try {
            return JWKSet.parse(getJSONString());
        } catch (ParseException e) {
            throw new TokenNotParseableJOSEJWTException(e);
        }
    }

    // ------------------------------------------ Fetch URL.

    /**
     * Refresh the JSON string.
     */
    private String getJSONString() {
        if ((this.expiryDateTime == null || DateUtility.dateTimeBeforeNow(this.expiryDateTime)) && DateUtility.dateTimeAfterNow(this.notRetryBeforeDateTime)) {
            try {
                LOGGER.debug("Fetching the JWKS from url {}.", this.url);
                this.lastJSONString = IOUtils.toString(new URL(url), StandardCharsets.UTF_8);
                this.notRetryBeforeDateTime = null;
                this.expiryDateTime = DateUtility.dateTimePlus(this.cacheValidityInSeconds, ChronoUnit.SECONDS);
            } catch (IOException e) {
                LOGGER.error("Unable to fetch the JWKS from the given URL.", e);
                this.notRetryBeforeDateTime = DateUtility.dateTimePlus(this.notRetryBeforeIntervalInSeconds, ChronoUnit.SECONDS);
            }
        }
        return this.lastJSONString;
    }

    /**
     * The builder for JWKS.
     * <P>
     * @author Julb.
     */
    public static class Builder {

        /**
         * The built instance
         */
        private RemoteUrlJWKSetProvider instance;

        // ------------------------------------------ Constructors.

        /**
         * Constructor.
         */
        public Builder() {
            super();
            this.instance = new RemoteUrlJWKSetProvider();
        }

        // ------------------------------------------ Getters/Setters.

        /**
         * Setter for property url.
         * @param url New value of property url.
         * @return the builder instance.
         */
        public Builder url(String url) {
            this.instance.url = url;
            return this;
        }

        /**
         * Setter for property cacheValidityInSeconds.
         * @param cacheValidityInSeconds New value of property cacheValidityInSeconds.
         * @return the builder instance.
         */
        public Builder cacheValidity(Integer cacheValidityInSeconds) {
            this.instance.cacheValidityInSeconds = cacheValidityInSeconds;
            return this;
        }

        /**
         * Returns the built instance.
         * @return the instance.
         */
        public RemoteUrlJWKSetProvider build() {
            return this.instance;
        }
    }
}
