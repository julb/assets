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

package me.julb.applications.announcement.services.impl;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import me.julb.applications.announcement.entities.AnnouncementEntity;
import me.julb.applications.announcement.repositories.AnnouncementRepository;
import me.julb.applications.announcement.services.RSSFeedService;
import me.julb.library.dto.rssfeed.RSSFeedDTO;
import me.julb.library.dto.rssfeed.RSSFeedItemDTO;
import me.julb.library.utility.date.DateUtility;
import me.julb.springbootstarter.core.configs.ConfigSourceService;
import me.julb.springbootstarter.core.context.TrademarkContextHolder;
import me.julb.springbootstarter.core.messages.MessageSourceService;
import me.julb.springbootstarter.core.rendering.ContentRenderService;

/**
 * The RSS feed service.
 * <P>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class RSSFeedServiceImpl implements RSSFeedService {

    /**
     * The config source service.
     */
    @Autowired
    private ConfigSourceService configSourceService;

    /**
     * The message resource.
     */
    @Autowired
    private MessageSourceService messageSourceService;

    /**
     * The content render service.
     */
    @Autowired
    private ContentRenderService contentRenderService;

    /**
     * The incident repository.
     */
    @Autowired
    private AnnouncementRepository announcementRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public RSSFeedDTO buildAnnouncementsFeed() {
        // Get current context.
        String tm = TrademarkContextHolder.getTrademark();
        Locale locale = LocaleContextHolder.getLocale();

        // Build RSS feed.
        RSSFeedDTO rssFeed = new RSSFeedDTO();
        rssFeed.setTitle(messageSourceService.getMessage("announcement.rss.feed.announcements.title", new Object[] {tm}, locale));
        rssFeed.setDescription(messageSourceService.getMessage("announcement.rss.feed.announcements.description", new Object[] {}, locale));
        rssFeed.setLanguage(locale.toLanguageTag());
        rssFeed.setLink(configSourceService.getProperty("announcement.rss.feed.announcements.link"));

        // Get announcements created in the last 30 days.
        List<AnnouncementEntity> announcements = announcementRepository.findByTmAndVisibilityDateTime_FromLessThanEqualOrderByLastUpdatedAtDesc(tm, DateUtility.dateTimeNow());
        for (AnnouncementEntity announcement : announcements) {
            // Get level
            String level = messageSourceService.getMessage("announcement.rss.feed.announcements.announcement.level." + announcement.getLevel(), new Object[] {}, locale);
            // Get message.
            String localizedMessageContentHtml = contentRenderService.renderToHtml(announcement.getLocalizedMessage());

            RSSFeedItemDTO item = new RSSFeedItemDTO();
            item.setId(announcement.getId());
            item.setTitle(messageSourceService.getMessage("announcement.rss.feed.announcements.announcement.title", new Object[] {level}, locale));
            item.getAuthor().setMail(announcement.getUser().getMail());
            item.getAuthor().setDisplayName(announcement.getUser().getDisplayName());
            item.setPublishedDateTime(announcement.getLastUpdatedAt());
            item.getCategories().add(messageSourceService.getMessage("announcement.rss.feed.announcements.announcement.category", new Object[] {}, locale));
            item.setHtmlDescription(localizedMessageContentHtml);

            rssFeed.getItems().add(item);
        }

        return rssFeed;
    }
}
