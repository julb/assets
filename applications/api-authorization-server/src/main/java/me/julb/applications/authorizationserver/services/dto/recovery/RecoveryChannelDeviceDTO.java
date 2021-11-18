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

package me.julb.applications.authorizationserver.services.dto.recovery;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The DTO representing a channel device.
 * <br>
 * @author Julb.
 */
@Getter
@Setter
@NoArgsConstructor
public class RecoveryChannelDeviceDTO extends RecoveryChannelDeviceRefDTO implements Comparable<RecoveryChannelDeviceDTO> {

    //@formatter:off
     /**
     * The maskedName attribute.
     * -- GETTER --
     * Getter for {@link #maskedName} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #maskedName} property.
     * @param maskedName the value to set.
     */
     //@formatter:on
    private String maskedName;

    /**
     * Default constructor.
     * @param id the channel identifier.
     * @param type the channel type.
     * @param maskedName the maskedName.
     */
    public RecoveryChannelDeviceDTO(String id, RecoveryChannelType type, String maskedName) {
        super(id, type);
        this.maskedName = maskedName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(RecoveryChannelDeviceDTO o) {
        if (getType().equals(o.getType())) {
            return this.maskedName.compareTo(o.getMaskedName());
        } else {
            return this.getType().toString().compareTo(o.getType().toString());
        }
    }
}
