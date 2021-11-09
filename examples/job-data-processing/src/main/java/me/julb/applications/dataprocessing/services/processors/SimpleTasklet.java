package me.julb.applications.dataprocessing.services.processors;

import lombok.extern.slf4j.Slf4j;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

/**
 * Simple tasklet example.
 * <br>
 * @author Julb.
 */
@Slf4j
public class SimpleTasklet implements Tasklet {

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
