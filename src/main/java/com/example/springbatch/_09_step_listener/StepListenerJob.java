package com.example.springbatch._09_step_listener;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableBatchProcessing
public class StepListenerJob {

    @Autowired
    private JobLauncher jobLauncher;
    //創造step對象的工廠
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    //創造一個step對象執行的任務
    @Autowired
    private StepBuilderFactory stepBuilderFactory;


  @Bean
    public Tasklet tasklet() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {


                System.out.println("---------1----------");

                return RepeatStatus.FINISHED; //會一直跑下去
            }
        };
    }

    //步驟監聽器
    public MyStepListener myStepListener(){
      return new MyStepListener();
    }

    @Bean
    public Step step1(){

        return stepBuilderFactory.get("step1")
                .tasklet(tasklet())
                .listener(myStepListener())
                .build();
    }


    @Bean
    public Job job(){

        return jobBuilderFactory.get("step-listener-job")
                .start(step1())
                .incrementer(new RunIdIncrementer())
                .build();
    }
    public static void main(String[] args) {

        SpringApplication.run(StepListenerJob.class,args);

    }
}
