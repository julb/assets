/**
 * Copyright (c) Julb.
 */
package me.julb.applications.dataprocessing.services.processors;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemWriter;

/**
 * The string item writer.
 * <P>
 * @author Julb.
 */
@Slf4j
public class StringItemWriter implements ItemWriter<String> {

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
