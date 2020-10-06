package me.julb.applications.helloworld.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.http.fileupload.FileItemIterator;
import org.apache.tomcat.util.http.fileupload.FileItemStream;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.util.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * The hello controller.
 * <P>
 * @author Julb.
 */
@RestController
@Validated
@RequestMapping
public class UploadController {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadController.class);

    // ------------------------------------------ Read methods.

    @RequestMapping(value = "/upload-streaming", method = RequestMethod.POST)
    public String handleUploadStreaming(HttpServletRequest request)
        throws Exception {
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        LOGGER.info("Request is multipart: {}.", isMultipart);
        // Create a factory for disk-based file items
        ServletFileUpload upload = new ServletFileUpload();
        try {
            FileItemIterator iterStream = upload.getItemIterator(request);
            while (iterStream.hasNext()) {
                FileItemStream item = iterStream.next();
                String name = item.getFieldName();
                InputStream input = item.openStream();
                OutputStream output = new FileOutputStream(new File("/tmp/" + UUID.randomUUID().toString()));
                if (!item.isFormField()) {
                    LOGGER.info("Starting copy of {}.", name);
                    IOUtils.copy(input, output);
                    LOGGER.info("Finishing copy.");
                } else {
                    // process form fields
                    String formFieldValue = Streams.asString(input);
                    LOGGER.info("Received form field {}={}", name, formFieldValue);
                }
            }
            return "success!";
        } catch (FileUploadException ex) {
            return "failed: " + ex.getMessage();
        }
    }
}
