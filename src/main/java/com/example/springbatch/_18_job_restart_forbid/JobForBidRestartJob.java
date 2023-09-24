package com.example.springbatch._18_job_restart_forbid;

import com.example.springbatch._17_job_stop_sign.ResourceCount;
import com.example.springbatch._17_job_stop_sign.SignJobStopJob;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
@SpringBootApplication
@EnableBatchProcessing
public class JobForBidRestartJob {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    //創造一個step對象執行的任務
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Tasklet tasklet1() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                System.out.println("---------tasklet1----------");

                    chunkContext.getStepContext().getStepExecution().setTerminateOnly();

                return RepeatStatus.FINISHED; //會一直跑下去
            }
        };
    }

    @Bean
    public Tasklet tasklet2() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                System.out.println("---------tasklet2----------");

                return RepeatStatus.FINISHED;
            }
        };
    }




    @Bean
    public Step step1(){

        return stepBuilderFactory.get("step1")
                .tasklet(tasklet1())
                .build();
    }

    @Bean
    public Step step2(){

        return stepBuilderFactory.get("step2")
                .tasklet(tasklet2())
                .build();
    }


    @Bean
    public Job job(){

        return jobBuilderFactory.get("sign-forbid-stop-job1")
                .preventRestart() //禁止重啟
                .start(step1())
                .next(step2())
                .build();
    }

    public static void main(String[] args) {

        SpringApplication.run(JobForBidRestartJob.class,args);

    }
}
