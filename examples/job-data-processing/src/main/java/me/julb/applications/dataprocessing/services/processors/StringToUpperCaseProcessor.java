/**
 * Copyright (c) Airbus.
 */
package me.julb.applications.dataprocessing.services.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

/**
 * The processor that converts a string to uppercase string.
 * <P>
 * @author Airbus.
 */
public class StringToUpperCaseProcessor implements ItemProcessor<String, String> {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(StringToUpperCaseProcessor.class);

    // ------------------------------------------ Overridden methods.

    /**
     * {@inheritDoc}
     */
    @Override
    public String process(String item)
        throws Exception {
        LOGGER.info("Uppercasing item <{}>.", item);
        return item.toUpperCase();
    }
}
