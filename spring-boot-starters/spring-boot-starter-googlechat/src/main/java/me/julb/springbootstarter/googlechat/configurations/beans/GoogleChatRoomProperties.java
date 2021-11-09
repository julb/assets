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
package me.julb.springbootstarter.googlechat.configurations.beans;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import me.julb.library.utility.validator.constraints.GoogleChatRoom;
import me.julb.library.utility.validator.constraints.GoogleChatThreadKey;

/**
 * The Google Chat Room properties configuration.
 * <br>
 * @author Julb.
 */
@Getter
@Setter
@NoArgsConstructor
public class GoogleChatRoomProperties {

    //@formatter:off
     /**
     * The name attribute.
     * -- GETTER --
     * Getter for {@link #name} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #name} property.
     * @param name the value to set.
     */
     //@formatter:on
    @NotNull
    @NotBlank
    @GoogleChatRoom
    private String name;

    //@formatter:off
     /**
     * The spaceId attribute.
     * -- GETTER --
     * Getter for {@link #spaceId} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #spaceId} property.
     * @param spaceId the value to set.
     */
     //@formatter:on
    @NotNull
    @NotBlank
    private String spaceId;

    //@formatter:off
     /**
     * The key attribute.
     * -- GETTER --
     * Getter for {@link #key} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #key} property.
     * @param key the value to set.
     */
     //@formatter:on
    @NotNull
    @NotBlank
    private String key;

    //@formatter:off
     /**
     * The token attribute.
     * -- GETTER --
     * Getter for {@link #token} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #token} property.
     * @param token the value to set.
     */
     //@formatter:on
    @NotNull
    @NotBlank
    private String token;

    //@formatter:off
     /**
     * The defaultThreadKey attribute.
     * -- GETTER --
     * Getter for {@link #defaultThreadKey} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #defaultThreadKey} property.
     * @param defaultThreadKey the value to set.
     */
     //@formatter:on
    @GoogleChatThreadKey
    private String defaultThreadKey;

}
