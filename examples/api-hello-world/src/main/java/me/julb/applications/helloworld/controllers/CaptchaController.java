package me.julb.applications.helloworld.controllers;

import io.swagger.v3.oas.annotations.Operation;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.julb.springbootstarter.googlerecaptcha.annotations.GoogleReCaptchaValid;

/**
 * The captcha controller.
 * <P>
 * @author Julb.
 */
@RestController
@Slf4j
@Validated
@RequestMapping(path = "/captcha", produces = MediaType.APPLICATION_JSON_VALUE)
public class CaptchaController {

    // ------------------------------------------ Read methods.

    /**
     * Performs a call through captcha protection.
     */
    @GetMapping
    @Operation(summary = "Captcha call")
    @GoogleReCaptchaValid
    public void test() {
        LOGGER.info("Receiving captcha call.");
    }

    // ------------------------------------------ Write methods.
}
