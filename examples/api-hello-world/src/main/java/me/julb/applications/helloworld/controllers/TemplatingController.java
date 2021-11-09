package me.julb.applications.helloworld.controllers;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.julb.springbootstarter.templating.services.TemplatingService;

/**
 * The captcha controller.
 * <br>
 * @author Julb.
 */
@RestController
@Validated
@RequestMapping(path = "/templating", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class TemplatingController {

    /**
     * The templating service.
     */
    @Autowired
    private TemplatingService templatingService;

    // ------------------------------------------ Read methods.

    /**
     * Performs a call.
     */
    @GetMapping
    public void test() {
        Map<String, String> params = new HashMap<>();
        params.put("value", "John");
        LOGGER.info("Text templating: {}.", templatingService.render("test-text.txt", params));
        LOGGER.info("Html templating: {}.", templatingService.render("test-html.html", params));
    }

    // ------------------------------------------ Write methods.

}
