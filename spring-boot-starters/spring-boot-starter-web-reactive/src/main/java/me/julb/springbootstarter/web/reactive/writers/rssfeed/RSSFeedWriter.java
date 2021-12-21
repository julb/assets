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

package me.julb.springbootstarter.web.reactive.writers.rssfeed;


import com.rometools.rome.feed.rss.Category;
import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Description;
import com.rometools.rome.feed.rss.Guid;
import com.rometools.rome.feed.rss.Item;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.WireFeedOutput;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.Element;
import org.springframework.http.MediaType;

import me.julb.library.dto.rssfeed.RSSFeedDTO;
import me.julb.library.dto.rssfeed.RSSFeedItemDTO;
import me.julb.library.utility.constants.Strings;
import me.julb.library.utility.date.DateUtility;
import me.julb.library.utility.exceptions.InternalServerErrorException;

import reactor.core.publisher.Mono;

/**
 * A RSS feed writer.
 * <br>
 *
 * @author Julb.
 */
public final class RSSFeedWriter implements Function<Mono<RSSFeedDTO>, Mono<String>> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<String> apply(Mono<RSSFeedDTO> input) {
        return input.flatMap(rssFeed -> {
            try {
                Channel feed = new Channel("rss_2.0");
                feed.setTitle(rssFeed.getTitle());
                feed.setDescription(rssFeed.getDescription());
                feed.setLink(rssFeed.getLink());
                feed.setLanguage(rssFeed.getLanguage());
                Element atomLink = new Element("link", "http://www.w3.org/2005/Atom");
                atomLink.setAttribute("rel", "self");
                atomLink.setAttribute("href", rssFeed.getLink());
                atomLink.setAttribute("type", "application/rss+xml");
                feed.getForeignMarkup().add(atomLink);
    
                List<RSSFeedItemDTO> items = rssFeed.getItems();
                Collections.sort(items, Collections.reverseOrder());
                List<Item> feedItems = items.stream().map(rssFeedItem -> {
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
                }).toList();
    
                // set items.
                feed.setItems(feedItems);
                
                WireFeedOutput feedOutput = new WireFeedOutput();
                return Mono.just(feedOutput.outputString(feed));
            } catch(FeedException e) {
                return Mono.error(new InternalServerErrorException(e));
            }
        });
    }
}
