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

package me.julb.library.dto.icsfeed;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * The ICS feed.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
public class ICSFeedDTO {

    //@formatter:off
     /**
     * The title attribute.
     * -- GETTER --
     * Getter for {@link #title} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #title} property.
     * @param title the value to set.
     */
     //@formatter:on
    private String title;

    //@formatter:off
     /**
     * The events attribute.
     * -- GETTER --
     * Getter for {@link #events} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #events} property.
     * @param events the value to set.
     */
     //@formatter:on
    private List<ICSFeedEventDTO> events = new ArrayList<ICSFeedEventDTO>();
}
