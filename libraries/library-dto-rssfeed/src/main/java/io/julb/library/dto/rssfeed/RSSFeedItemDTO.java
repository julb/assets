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

package io.julb.library.dto.rssfeed;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * The RSS feed item.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
public class RSSFeedItemDTO implements Comparable<RSSFeedItemDTO> {

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
    private String id;

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
     * The link attribute.
     * -- GETTER --
     * Getter for {@link #link} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #link} property.
     * @param link the value to set.
     */
     //@formatter:on
    private String link;

    //@formatter:off
     /**
     * The publishedDateTime attribute.
     * -- GETTER --
     * Getter for {@link #publishedDateTime} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #publishedDateTime} property.
     * @param publishedDateTime the value to set.
     */
     //@formatter:on
    private String publishedDateTime;

    //@formatter:off
     /**
     * The htmlDescription attribute.
     * -- GETTER --
     * Getter for {@link #htmlDescription} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #htmlDescription} property.
     * @param htmlDescription the value to set.
     */
     //@formatter:on
    private String htmlDescription;

    //@formatter:off
     /**
     * The author attribute.
     * -- GETTER --
     * Getter for {@link #author} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #author} property.
     * @param author the value to set.
     */
     //@formatter:on
    private RSSFeedItemAuthorDTO author = new RSSFeedItemAuthorDTO();

    //@formatter:off
     /**
     * The categories attribute.
     * -- GETTER --
     * Getter for {@link #categories} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #categories} property.
     * @param categories the value to set.
     */
     //@formatter:on
    private List<String> categories = new ArrayList<String>();

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(RSSFeedItemDTO o) {
        return this.publishedDateTime.compareTo(o.getPublishedDateTime());
    }
}
