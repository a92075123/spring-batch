package com.example.springbatch._14_job_start_test;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;

@SpringBootTest(classes = App.class)
public class StartJobTest {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    //創造step對象的工廠
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    //創造一個step對象執行的任務
    public Tasklet tasklet() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                //要執行邏輯--step步驟執行邏輯
                System.out.println("hello world");
                return RepeatStatus.FINISHED; //通知執行完了
            }
        };
    }

    public Step step1(){
        //tasklet  執行step的邏輯，類似屬於thread()--->可以執行runable
        return stepBuilderFactory.get("step1").tasklet(tasklet())
                .build();
    }


    public Step step2(){
        return stepBuilderFactory.get("step1").tasklet(tasklet()).build();
    }


    public Job job(){
        return jobBuilderFactory.get("start-test-job").start(step1()).build();
    }

    @Test
    public void testStart() throws Exception {
        jobLauncher.run(job(),new JobParameters());
    }

}
