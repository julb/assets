/**
 * MIT License
 *
 * Copyright (c) 2017-2019 Julb
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.julb.applications.dataprocessing.configurations;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import me.julb.applications.dataprocessing.configurations.beans.CustomJobExecutionListener;
import me.julb.applications.dataprocessing.services.processors.SimpleTasklet;
import me.julb.applications.dataprocessing.services.processors.StringItemReader;
import me.julb.applications.dataprocessing.services.processors.StringItemWriter;
import me.julb.applications.dataprocessing.services.processors.StringToUpperCaseProcessor;

/**
 * The local configuration.
 * <P>
 * @author Julb.
 */
@Configuration
public class CustomConfiguration {

    /**
     * The factory to build jobs.
     */
    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    /**
     * The factory to build job steps.
     */
    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    /**
     * The task executor.
     */
    @Autowired
    public TaskExecutor taskExecutor;

    /**
     * The job execution listener.
     * @return the job execution listener.
     */
    @Bean
    public JobExecutionListener jobExecutionListener() {
        return new CustomJobExecutionListener();
    }

    /**
     * The certificate artifact reader.
     * @return the certificate artifact reader.
     */
    @Bean
    public ItemReader<String> itemReader() {
        return new StringItemReader();
    }

    /**
     * The certificate artifact to certificate processor.
     * @return the certificate artifact to certificate processor.
     */
    @Bean
    public ItemProcessor<String, String> itemProcessor() {
        return new StringToUpperCaseProcessor();
    }

    /**
     * The certificate ingestor.
     * @return the certificate ingestor.
     */
    @Bean
    public ItemWriter<String> itemWriter() {
        return new StringItemWriter();
    }

    /**
     * The simple tasklet.
     * @return the step.
     */
    @Bean
    public Tasklet simpleTasklet() {
        return new SimpleTasklet();
    }

    /**
     * The step to ingest vulnerability steps.
     * @return the step to ingest the vulnerability step.
     */
    @Bean
    public Step dataProcessingStep() {
        //@formatter:off
        return this.stepBuilderFactory.get("dataProcessing")
                    .<String, String>chunk(5)
                    .reader(itemReader())
                    .processor(itemProcessor())
                    .writer(itemWriter())
                    .build();
        //@formatter:on
    }

    /**
     * Step to use a tasklet.
     * @return the step.
     */
    @Bean
    public Step simpleStep() {
        //@formatter:off
        return stepBuilderFactory
            .get("simpleStep")
            .tasklet(simpleTasklet())
            .allowStartIfComplete(true)
            .build();
        //@formatter:on
    }

    /**
     * Define a flow to parallelize multiple flows.
     * @return the parallel flow.
     */
    @Bean
    public Flow parallelFlow() {
        //@formatter:off
        return new FlowBuilder<SimpleFlow>("splitFlow")
            .split(new SimpleAsyncTaskExecutor())
            .add(flow1(), flow2())
            .build();
        //@formatter:on
    }

    /**
     * The first flow.
     * @return the first flow.
     */
    @Bean
    public Flow flow1() {
        //@formatter:off
        return new FlowBuilder<SimpleFlow>("flow1")
            .start(dataProcessingStep())
            .build();
        //@formatter:on
    }

    /**
     * The second flow.
     * @return the second flow.
     */
    @Bean
    public Flow flow2() {
        //@formatter:off
        return new FlowBuilder<SimpleFlow>("flow2")
            .start(simpleStep())
            .build();
        //@formatter:on
    }

    /**
     * Main job.
     * @return the job
     */
    @Bean
    public Job mainJob() {
        //@formatter:off
        return this.jobBuilderFactory
            .get("mainJob")
                .incrementer(new RunIdIncrementer())
                .listener(jobExecutionListener())
                .start(parallelFlow())
            .end()
            .build();
        //@formatter:on
    }
}
