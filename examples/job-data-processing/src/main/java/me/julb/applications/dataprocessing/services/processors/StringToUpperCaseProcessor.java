/**
 * Copyright (c) Julb.
 */
package me.julb.applications.dataprocessing.services.processors;

import lombok.extern.slf4j.Slf4j;

import org.springframework.batch.item.ItemProcessor;

/**
 * The processor that converts a string to uppercase string.
 * <P>
 * @author Julb.
 */
@Slf4j
public class StringToUpperCaseProcessor implements ItemProcessor<String, String> {

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
