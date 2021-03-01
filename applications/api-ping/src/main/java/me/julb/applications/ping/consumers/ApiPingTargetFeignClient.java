package me.julb.applications.ping.consumers;

import org.springframework.web.bind.annotation.GetMapping;

import me.julb.library.dto.simple.message.MessageDTO;

/**
 * A Feign client to ping a remote ping application.
 * <P>
 * @author Julb.
 */
public interface ApiPingTargetFeignClient {

    /**
     * Pings someone.
     * @return the message.
     */
    @GetMapping("/ping")
    MessageDTO ping();

}
