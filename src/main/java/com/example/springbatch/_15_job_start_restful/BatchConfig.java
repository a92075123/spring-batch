package com.example.springbatch._15_job_start_restful;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableBatchProcessing
@Configuration
public class BatchConfig {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    //創造step對象的工廠
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    //創造一個step對象執行的任務
    @Bean
    public Tasklet tasklet() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                //要執行邏輯--step步驟執行邏輯
                System.out.println("hello world"+chunkContext.getStepContext().getJobParameters().get("name"));
                return RepeatStatus.FINISHED; //通知執行完了
            }
        };
    }
    @Bean
    public Step step1(){
        //tasklet  執行step的邏輯，類似屬於thread()--->可以執行runable
        return stepBuilderFactory.get("step1").tasklet(tasklet())
                .build();
    }

    @Bean
    public Step step2(){
        return stepBuilderFactory.get("step1").tasklet(tasklet()).build();
    }

    @Bean
    public Job job(){
        return jobBuilderFactory.get("hello-restful-job")
                .start(step1())
                .incrementer(new RunIdIncrementer())
                .build();

    }

}
