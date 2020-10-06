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

package me.julb.springbootstarter.web.views.rssfeed;

import com.rometools.rome.feed.rss.Category;
import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Description;
import com.rometools.rome.feed.rss.Guid;
import com.rometools.rome.feed.rss.Item;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.Element;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.view.feed.AbstractRssFeedView;

import me.julb.library.dto.rssfeed.RSSFeedDTO;
import me.julb.library.dto.rssfeed.RSSFeedItemDTO;
import me.julb.library.utility.constants.Strings;
import me.julb.library.utility.date.DateUtility;

/**
 * The RSS feed view based on the {@link RSSFeedDTO}.
 * <P>
 * @author Julb.
 */
public class RSSFeedView extends AbstractRssFeedView {

    /**
     * The RSS feed.
     */
    private RSSFeedDTO rssFeed;

    /**
     * Default constructor.
     * @param rssFeed the RSS feed.
     */
    public RSSFeedView(RSSFeedDTO rssFeed) {
        super();
        this.rssFeed = rssFeed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void buildFeedMetadata(Map<String, Object> model, Channel feed, HttpServletRequest request) {
        feed.setTitle(rssFeed.getTitle());
        feed.setDescription(rssFeed.getDescription());
        feed.setLink(rssFeed.getLink());
        feed.setLanguage(rssFeed.getLanguage());
        Element atomLink = new Element("link", "http://www.w3.org/2005/Atom");
        atomLink.setAttribute("rel", "self");
        atomLink.setAttribute("href", rssFeed.getLink());
        atomLink.setAttribute("type", "application/rss+xml");
        feed.getForeignMarkup().add(atomLink);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<Item> buildFeedItems(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        List<RSSFeedItemDTO> items = rssFeed.getItems();
        Collections.sort(items, Collections.reverseOrder());
        return items.stream().map((rssFeedItem) -> {
            Item romeItem = new Item();

            String author = StringUtils.join(rssFeedItem.getAuthor().getMail(), Strings.SPACE, Strings.LEFT_PARENTHESIS, rssFeedItem.getAuthor().getDisplayName(), Strings.RIGHT_PARENTHESIS);
            romeItem.setAuthor(author);

            for (String category : rssFeedItem.getCategories()) {
                Category romeCategory = new Category();
                romeCategory.setValue(category);
                romeItem.getCategories().add(romeCategory);
            }

            Description description = new Description();
            description.setType(MediaType.TEXT_HTML_VALUE);
            description.setValue(rssFeedItem.getHtmlDescription());
            romeItem.setDescription(description);

            Guid guid = new Guid();
            guid.setValue(rssFeedItem.getId());
            guid.setPermaLink(false);
            romeItem.setGuid(guid);

            romeItem.setLink(rssFeedItem.getLink());

            romeItem.setPubDate(DateUtility.parseDateTime(rssFeedItem.getPublishedDateTime()));

            romeItem.setTitle(rssFeedItem.getTitle());
            return romeItem;
        }).collect(Collectors.toList());
    }
}
