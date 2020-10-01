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

package io.julb.library.persistence.mongodb.entities.mobilephone;

import io.julb.library.dto.simple.mobilephone.MobilePhoneCreationDTO;
import io.julb.library.dto.simple.mobilephone.MobilePhoneDTO;
import io.julb.library.mapping.annotations.ObjectMappingFactory;
import io.julb.library.utility.interfaces.IPhoneNumber;
import io.julb.library.utility.validator.constraints.PhoneNumber;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * The mobile phone entity.
 * <P>
 * @author Julb.
 */
@ObjectMappingFactory(creation = MobilePhoneCreationDTO.class, read = MobilePhoneDTO.class)
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false, of = {"countryCode", "number"})
@NoArgsConstructor
@PhoneNumber
public class MobilePhoneEntity implements IPhoneNumber {

    //@formatter:off
     /**
     * The countryCode attribute.
     * -- GETTER --
     * Getter for {@link #countryCode} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #countryCode} property.
     * @param countryCode the value to set.
     */
     //@formatter:on
    @NotNull
    @NotBlank
    @Pattern(regexp = "^[A-Z]+$")
    @Size(min = 2, max = 3)
    private String countryCode;

    //@formatter:off
     /**
     * The number attribute.
     * -- GETTER --
     * Getter for {@link #number} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #number} property.
     * @param number the value to set.
     */
     //@formatter:on
    @NotNull
    @NotBlank
    @Pattern(regexp = "^[0-9]+$")
    @Size(min = 1, max = 20)
    private String number;

    //@formatter:off
     /**
     * The internationalNumber attribute.
     * -- GETTER --
     * Getter for {@link #internationalNumber} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #internationalNumber} property.
     * @param internationalNumber the value to set.
     */
     //@formatter:on
    @NotNull
    @NotBlank
    private String internationalNumber;

    //@formatter:off
     /**
     * The nationalNumber attribute.
     * -- GETTER --
     * Getter for {@link #nationalNumber} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #nationalNumber} property.
     * @param nationalNumber the value to set.
     */
     //@formatter:on
    @NotNull
    @NotBlank
    private String nationalNumber;

    //@formatter:off
     /**
     * The e164Number attribute.
     * -- GETTER --
     * Getter for {@link #e164Number} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #e164Number} property.
     * @param e164Number the value to set.
     */
     //@formatter:on
    @NotNull
    @NotBlank
    private String e164Number;

}
