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
package me.julb.springbootstarter.googlechat.services.impl;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import me.julb.library.dto.googlechat.GoogleChatMessageDTO;
import me.julb.library.utility.exceptions.ResourceNotFoundException;
import me.julb.springbootstarter.googlechat.annotations.ConditionalOnGoogleChatEnabled;
import me.julb.springbootstarter.googlechat.configurations.beans.GoogleChatProperties;
import me.julb.springbootstarter.googlechat.configurations.beans.GoogleChatRoomProperties;
import me.julb.springbootstarter.googlechat.repositories.GoogleChatRepository;
import me.julb.springbootstarter.googlechat.services.GoogleChatService;

/**
 * GChat service implementation.
 * <br>
 * @author Julb.
 */
@Service
@Validated
@Slf4j
@ConditionalOnGoogleChatEnabled
public class GoogleChatServiceImpl implements GoogleChatService {

    /**
     * The Google Chat properties.
     */
    @Autowired
    private GoogleChatProperties googleChatProperties;

    /**
     * The Google chat repository.
     */
    @Autowired
    private GoogleChatRepository googleChatRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(@NotNull @Valid GoogleChatMessageDTO messageDto) {
        LOGGER.debug("Sending GChat message : {}.", messageDto);

        // Find room.
        String room = messageDto.getRoom();
        GoogleChatRoomProperties roomProperties = getRoomProperties(room);
        if (roomProperties == null) {
            LOGGER.warn("No room configured for room {}.", room);
            throw new ResourceNotFoundException(GoogleChatRoomProperties.class, "room", room);
        }

        // Determine threadKey.
        String threadKey = messageDto.getThreadKey();
        if (StringUtils.isBlank(threadKey) && StringUtils.isNotBlank(roomProperties.getDefaultThreadKey())) {
            LOGGER.debug("Using default thread key defined for room {}: {}.", room, roomProperties.getDefaultThreadKey());
            threadKey = roomProperties.getDefaultThreadKey();
        }

        // Build body.

        // Send message.
        //@formatter:off
        googleChatRepository.createTextMessage(
            roomProperties.getSpaceId(), 
            roomProperties.getKey(), 
            roomProperties.getToken(), 
            threadKey, 
            messageDto.getText()
        );
        //@formatter:off

        LOGGER.debug("Message sent successfully.");
    }

    /**
     * Gets the room properties for the given room name.
     * @param room the room name.
     * @return the google chat room properties, <code>null</code> if not found.
     */
    private GoogleChatRoomProperties getRoomProperties(String room) {
        //@formatter:off
        return googleChatProperties
            .getRooms()
            .stream()
            .filter(roomProperties -> StringUtils.equalsIgnoreCase(roomProperties.getName(), room))
            .findFirst()
            .orElse(null);
        //@formatter:off
    }
}
