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
package me.julb.library.utility.http;

import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import me.julb.library.utility.constants.Strings;

/**
 * A generic configuration class for a SSL client.
 * <br>
 * @author Julb.
 */
public final class HttpHeaderUtility {

    /**
     * The 'basic' value.
     */
    public static final String BASIC = "Basic";

    /**
     * The pattern matcher to extract basic token value.
     */
    private static final Pattern BASIC_TOKEN_PATTERN_MATCHER = Pattern.compile("^Basic\\s([a-zA-Z0-9+\\/\\-_=]+)$");

    /**
     * The pattern matcher to extract basic username and password value.
     */
    private static final Pattern BASIC_USERNAME_PASSWORD_PATTERN_MATCHER = Pattern.compile("^([^:]+):(.*)$");

    /**
     * The 'bearer' value.
     */
    public static final String BEARER = "Bearer";

    /**
     * The pattern matcher to extract bearer token value.
     */
    private static final Pattern BEARER_TOKEN_PATTERN_MATCHER = Pattern.compile("^Bearer\\s([a-zA-Z0-9.\\-_=]+)$");

    /**
     * Builds a bearer token.
     * @param token the token.
     * @return the bearer token value.
     */
    public static String toBearerToken(String token) {
        if (StringUtils.isBlank(token)) {
            return null;
        }
        return StringUtils.joinWith(Strings.SPACE, BEARER, token);
    }

    /**
     * Checks if the given string is a bearer token.
     * @param bearerToken the bearer token.
     * @return the token value.
     */
    public static boolean isBearerToken(String bearerToken) {
        return fromBearerToken(bearerToken) != null;
    }

    /**
     * Gets the token from a bearer token.
     * @param bearerToken the bearer token.
     * @return the token value.
     */
    public static String fromBearerToken(String bearerToken) {
        if (StringUtils.isBlank(bearerToken)) {
            return null;
        }
        Matcher matcher = BEARER_TOKEN_PATTERN_MATCHER.matcher(bearerToken);
        if (matcher.matches()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

    /**
     * Builds a Basic token.
     * @param userName the user name.
     * @param password the password.
     * @return the Basic auth token value.
     */
    public static String toBasicToken(String userName, String password) {
        if (StringUtils.isBlank(userName) || StringUtils.isBlank(password)) {
            return null;
        }
        String token = Base64.encodeBase64URLSafeString(StringUtils.joinWith(Strings.SEMICOLON, userName, password).getBytes(StandardCharsets.UTF_8));
        return StringUtils.joinWith(Strings.SPACE, BASIC, token);
    }

    /**
     * Gets the username and password from a basic token.
     * @param basicToken the basic token.
     * @return the userName as left value, the password as right value.
     */
    public static Pair<String, String> fromBasicToken(String basicToken) {
        if (StringUtils.isBlank(basicToken)) {
            return null;
        }
        Matcher matcher = BASIC_TOKEN_PATTERN_MATCHER.matcher(basicToken);
        if (matcher.matches()) {
            String token = matcher.group(1);
            String userNameWithPassword = new String(Base64.decodeBase64(token), StandardCharsets.UTF_8);
            Matcher userNamePasswordMatcher = BASIC_USERNAME_PASSWORD_PATTERN_MATCHER.matcher(userNameWithPassword);
            if (userNamePasswordMatcher.matches()) {
                return new ImmutablePair<String, String>(userNamePasswordMatcher.group(1), userNamePasswordMatcher.group(2));
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}