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

package io.julb.springbootstarter.ics.service.impl;

import io.julb.library.dto.icsfeed.ICSFeedDTO;
import io.julb.library.dto.icsfeed.ICSFeedEventDTO;
import io.julb.library.utility.date.DateUtility;
import io.julb.library.utility.exceptions.InternalServerErrorException;
import io.julb.springbootstarter.ics.service.ICSFeedWriterService;

import java.io.IOException;
import java.io.OutputStream;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.MapTimeZoneCache;

/**
 * The ICS feed writer service implementation
 * <P>
 * @author Julb.
 */
@Service
public class ICSFeedWriterServiceImpl implements ICSFeedWriterService {

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(ICSFeedDTO icsFeed, OutputStream outputStream) {
        // Set property.
        System.setProperty("net.fortuna.ical4j.timezone.cache.impl", MapTimeZoneCache.class.getName());

        try {
            // Create a calendar
            net.fortuna.ical4j.model.Calendar icsCalendar = new net.fortuna.ical4j.model.Calendar();
            icsCalendar.getProperties().add(new ProdId(String.format("-//%s//iCal4j 1.0//%s", icsFeed.getTitle(), LocaleContextHolder.getLocale().toLanguageTag().toUpperCase())));
            icsCalendar.getProperties().add(CalScale.GREGORIAN);
            icsCalendar.getProperties().add(Version.VERSION_2_0);

            // Create a TimeZone
            TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
            TimeZone timezone = registry.getTimeZone(LocaleContextHolder.getTimeZone().getID());
            VTimeZone tz = timezone.getVTimeZone();

            for (ICSFeedEventDTO icsFeedEvent : icsFeed.getEvents()) {
                // Create the event
                DateTime start = new DateTime(DateUtility.parseDateTime(icsFeedEvent.getDateTimeInterval().getFrom()), timezone);
                DateTime end = new DateTime(DateUtility.parseDateTime(icsFeedEvent.getDateTimeInterval().getTo()), timezone);

                VEvent meeting = new VEvent(start, end, icsFeedEvent.getTitle());
                meeting.getProperties().add(tz.getTimeZoneId());
                meeting.getProperties().add(new Uid(icsFeedEvent.getId()));
                meeting.getProperties().add(new Description(icsFeedEvent.getDescription()));

                // Add the event to the calendar.
                icsCalendar.getComponents().add(meeting);
            }

            // Output calendar
            CalendarOutputter outputter = new CalendarOutputter();
            outputter.output(icsCalendar, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            throw new InternalServerErrorException(e);
        }
    }
}
