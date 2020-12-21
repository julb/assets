/**
 * MIT License
 *
 * Copyright (c) 2017-2019 Julb
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

package me.julb.springbootstarter.googlerecaptcha.consumers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The DTO that wraps Google ReCaptcha v3 challenge response.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleReCaptchaV3ChallengeResponseDTO {

    //@formatter:off
     /**
     * The success attribute.
     * -- GETTER --
     * Getter for {@link #success} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #success} property.
     * @param success the value to set.
     */
     //@formatter:on
    @JsonProperty("success")
    private boolean success;

    //@formatter:off
     /**
     * The challengeTs attribute.
     * -- GETTER --
     * Getter for {@link #challengeTs} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #challengeTs} property.
     * @param challengeTs the value to set.
     */
     //@formatter:on
    @JsonProperty("challenge_ts")
    private String challengeTs;

    //@formatter:off
     /**
     * The hostname attribute.
     * -- GETTER --
     * Getter for {@link #hostname} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #hostname} property.
     * @param hostname the value to set.
     */
     //@formatter:on
    @JsonProperty("hostname")
    private String hostname;

    //@formatter:off
     /**
     * The score attribute.
     * -- GETTER --
     * Getter for {@link #score} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #score} property.
     * @param score the value to set.
     */
     //@formatter:on
    @JsonProperty("score")
    private float score;

    //@formatter:off
     /**
     * The action attribute.
     * -- GETTER --
     * Getter for {@link #action} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #action} property.
     * @param action the value to set.
     */
     //@formatter:on
    @JsonProperty("action")
    private String action;

    //@formatter:off
     /**
     * The errorCodes attribute.
     * -- GETTER --
     * Getter for {@link #errorCodes} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #errorCodes} property.
     * @param errorCodes the value to set.
     */
     //@formatter:on
    @JsonProperty("error-codes")
    private String[] errorCodes;
}
