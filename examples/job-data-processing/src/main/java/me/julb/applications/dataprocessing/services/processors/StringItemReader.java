/**
 * Copyright (c) Airbus.
 */
package me.julb.applications.dataprocessing.services.processors;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.database.AbstractPagingItemReader;

import me.julb.applications.dataprocessing.configurations.beans.CustomJobExecutionListener;
import me.julb.applications.dataprocessing.configurations.beans.CustomJobExecutionMetrics;

/**
 * A String item reader example.
 * <P>
 * @author Airbus.
 */
public class StringItemReader extends AbstractPagingItemReader<String> {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(StringItemReader.class);

    /**
     * The current job execution.
     */
    private JobExecution jobExecution;

    // ------------------------------------------ Overridden methods.

    /**
     * Method invoked before the step execution.
     * @param stepExecution the step execution.
     */
    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        jobExecution = stepExecution.getJobExecution();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doReadPage() {
        LOGGER.info("Fetching data <page={}, size={}>.", getPage(), getPageSize());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }

        // Data
        @SuppressWarnings("unchecked")
        List<String> data = (List<String>) jobExecution.getExecutionContext().get("data");

        // Build a page request.
        int total = data.size();
        int offset = getPage() * getPageSize();
        List<String> subList;
        if (offset < total) {
            int fromIndex = offset;
            int toIndex = Math.min(total, offset + getPageSize());
            subList = data.subList(fromIndex, toIndex);
        } else {
            subList = new ArrayList<String>();
        }

        // Log receive request.
        LOGGER.info("Fetched <{}> items from <page={}, size={}>.", subList.size(), getPage(), getPageSize());

        // Get job metrics
        CustomJobExecutionMetrics metrics = (CustomJobExecutionMetrics) jobExecution.getExecutionContext().get(CustomJobExecutionListener.JOB_EXECUTION_CONTEXT_METRICS);
        metrics.setProcessedItemsCount(metrics.getProcessedItemsCount() + subList.size());

        // Return results.
        results = subList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doJumpToPage(int itemIndex) {
        // NOOP
    }
}
