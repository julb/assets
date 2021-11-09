package me.julb.applications.helloworld.controllers;

import io.swagger.v3.oas.annotations.Operation;

import java.util.Arrays;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.julb.library.dto.mail.MailAttachmentDTO;
import me.julb.library.dto.mail.MailDTO;
import me.julb.library.dto.mail.MailInlineAttachmentDTO;
import me.julb.springbootstarter.mail.services.MailService;

/**
 * The captcha controller.
 * <br>
 * @author Julb.
 */
@RestController
@Slf4j
@Validated
@RequestMapping(path = "/mail", produces = MediaType.APPLICATION_JSON_VALUE)
public class MailController {

    /**
     * The mail service.
     */
    @Autowired
    private MailService mailService;

    /**
     * The userMail attribute.
     */
    @Value("${spring.mail.username}")
    private String userMail;

    // ------------------------------------------ Read methods.

    /**
     * Sends an email.
     * @throws Exception if an error occurs.
     */
    @GetMapping
    @Operation(summary = "Mail send")
    public void test()
        throws Exception {
        LOGGER.info("Sending email.");

        MailDTO m = new MailDTO();
        m.setFrom(userMail);
        m.setTos(Arrays.asList(userMail));
        m.setSubject("test");
        // m.setHtml("<html><body><h1>lol</h1></body></html>");
        m.setHtml("<html><body><img src='cid:hello.png'><br><h1>lol</h1></body></html>");

        MailInlineAttachmentDTO a = new MailInlineAttachmentDTO();
        a.setMimeType("image/png");
        a.setContentId("hello.png");
        a.setContentBase64(Base64.encodeBase64String(IOUtils.toByteArray(getClass().getResourceAsStream("/hello.png"))));
        m.getInlineAttachments().add(a);

        MailAttachmentDTO a2 = new MailAttachmentDTO();
        a2.setMimeType("text/pdf");
        a2.setFileName("Test.pdf");
        a2.setContentBase64(Base64.encodeBase64String(IOUtils.toByteArray(getClass().getResourceAsStream("/Document.pdf"))));
        m.getAttachments().add(a2);

        mailService.send(m);
    }

    // ------------------------------------------ Write methods.
}
