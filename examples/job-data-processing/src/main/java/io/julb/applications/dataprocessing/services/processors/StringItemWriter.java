/**
 * Copyright (c) Airbus.
 */
package io.julb.applications.dataprocessing.services.processors;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemWriter;

/**
 * The string item writer.
 * <P>
 * @author Airbus.
 */
public class StringItemWriter implements ItemWriter<String> {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(StringItemWriter.class);

    // ------------------------------------------ Overridden methods.

    /**
     * Method invoked before the step execution.
     * @param stepExecution the step execution.
     */
    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(List<? extends String> items)
        throws Exception {
        for (String item : items) {
            LOGGER.info("Writing {}...", item);
        }
    }
}
