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

package me.julb.applications.authorizationserver.entities.session;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import me.julb.applications.authorizationserver.entities.UserEntity;
import me.julb.library.persistence.mongodb.entities.AbstractAuditedEntity;
import me.julb.library.utility.interfaces.IIdentifiable;
import me.julb.library.utility.validator.constraints.DateTimeISO8601;
import me.julb.library.utility.validator.constraints.IPAddress;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.library.utility.validator.constraints.Trademark;

/**
 * The user session entity.
 * <br>
 * @author Julb.
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false, of = "id")
@NoArgsConstructor
@Document("users-sessions")
public class UserSessionEntity extends AbstractAuditedEntity implements IIdentifiable {

    //@formatter:off
     /**
     * The id attribute.
     * -- GETTER --
     * Getter for {@link #id} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #id} property.
     * @param id the value to set.
     */
     //@formatter:on
    @Id
    @Identifier
    private String id;

    //@formatter:off
     /**
     * The tm attribute.
     * -- GETTER --
     * Getter for {@link #tm} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #tm} property.
     * @param tm the value to set.
     */
     //@formatter:on
    @NotNull
    @NotBlank
    @Trademark
    private String tm;

    //@formatter:off
     /**
     * The user attribute.
     * -- GETTER --
     * Getter for {@link #user} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #user} property.
     * @param user the value to set.
     */
     //@formatter:on
    @NotNull
    @DBRef
    private UserEntity user;

    //@formatter:off
     /**
     * The expiryDateTime attribute.
     * -- GETTER --
     * Getter for {@link #expiryDateTime} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #expiryDateTime} property.
     * @param expiryDateTime the value to set.
     */
     //@formatter:on
    @DateTimeISO8601
    private String expiryDateTime;

    //@formatter:off
     /**
     * The durationInSeconds attribute.
     * -- GETTER --
     * Getter for {@link #durationInSeconds} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #durationInSeconds} property.
     * @param durationInSeconds the value to set.
     */
     //@formatter:on
    @NotNull
    @Min(1)
    private Long durationInSeconds;

    //@formatter:off
     /**
     * The mfaVerified attribute.
     * -- GETTER --
     * Getter for {@link #mfaVerified} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #mfaVerified} property.
     * @param mfaVerified the value to set.
     */
     //@formatter:on
    @NotNull
    private Boolean mfaVerified;

    //@formatter:off
     /**
     * The securedIdToken attribute.
     * -- GETTER --
     * Getter for {@link #securedIdToken} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #securedIdToken} property.
     * @param securedIdToken the value to set.
     */
     //@formatter:on
    @NotNull
    @NotBlank
    @Size(max = 128)
    private String securedIdToken;

    //@formatter:off
     /**
     * The ipv4Address attribute.
     * -- GETTER --
     * Getter for {@link #ipv4Address} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #ipv4Address} property.
     * @param ipv4Address the value to set.
     */
     //@formatter:on
    @IPAddress
    private String ipv4Address;

    //@formatter:off
     /**
     * The lastUseDateTime attribute.
     * -- GETTER --
     * Getter for {@link #lastUseDateTime} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #lastUseDateTime} property.
     * @param lastUseDateTime the value to set.
     */
     //@formatter:on
    @NotNull
    @NotBlank
    @DateTimeISO8601
    private String lastUseDateTime;

    //@formatter:off
     /**
     * The operatingSystem attribute.
     * -- GETTER --
     * Getter for {@link #operatingSystem} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #operatingSystem} property.
     * @param operatingSystem the value to set.
     */
     //@formatter:on
    @Size(max = 32)
    private String operatingSystem;

    //@formatter:off
     /**
     * The browser attribute.
     * -- GETTER --
     * Getter for {@link #browser} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #browser} property.
     * @param browser the value to set.
     */
     //@formatter:on
    @Size(max = 32)
    private String browser;

}
