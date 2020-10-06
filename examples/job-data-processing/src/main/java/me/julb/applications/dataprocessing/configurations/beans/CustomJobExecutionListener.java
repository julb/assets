package me.julb.applications.dataprocessing.configurations.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Value;

/**
 * A listener for the current job execution.
 * <P>
 * @author Airbus.
 */
public class CustomJobExecutionListener implements JobExecutionListener {

    /**
     * The key to get the metrics of the job.
     */
    public static final String JOB_EXECUTION_CONTEXT_METRICS = "metrics";

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomJobExecutionListener.class);

    /**
     * The application name.
     */
    @Value("${spring.application.name}")
    private String applicationName;

    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeJob(JobExecution jobExecution) {
        // Register metrics
        jobExecution.getExecutionContext().put(CustomJobExecutionListener.JOB_EXECUTION_CONTEXT_METRICS, new CustomJobExecutionMetrics());

        // Bootstrap some data
        List<String> data = new ArrayList<String>();
        for (int i = 0; i < 103; i++) {
            data.add(i + " - " + UUID.randomUUID().toString());
        }
        jobExecution.getExecutionContext().put("data", data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterJob(JobExecution jobExecution) {
        // Get job metrics
        if (jobExecution.getStatus().isUnsuccessful()) {
            LOGGER.error("Job completed in error with status <{}>.", jobExecution.getStatus());
        } else {
            LOGGER.info("Job completed successfully with status <{}>.", jobExecution.getStatus());
            CustomJobExecutionMetrics metrics = (CustomJobExecutionMetrics) jobExecution.getExecutionContext().get(CustomJobExecutionListener.JOB_EXECUTION_CONTEXT_METRICS);
            LOGGER.info("Metrics -----> {} items processed.", metrics.getProcessedItemsCount());
        }

    }

}
