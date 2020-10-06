package me.julb.applications.dataprocessing.services.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

/**
 * Simple tasklet example.
 * <P>
 * @author Airbus.
 */
public class SimpleTasklet implements Tasklet {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleTasklet.class);

    /**
     * Runs the indexation. {@inheritDoc}
     */
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
        throws Exception {
        LOGGER.info("Operation started.");

        Thread.sleep(3000);

        LOGGER.info("Operation completed.");

        // Update status
        return RepeatStatus.FINISHED;
    }
}
