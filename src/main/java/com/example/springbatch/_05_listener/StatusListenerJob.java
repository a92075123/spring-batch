package com.example.springbatch._05_listener;

import com.example.springbatch._01_hello.HelloJob;
import com.example.springbatch._05_listener.JobStateListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.JobListenerFactoryBean;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Map;

@SpringBootApplication
@EnableBatchProcessing
public class StatusListenerJob {
    //創建Job對象的工廠
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

                JobExecution jobExecution = stepContribution.getStepExecution().getJobExecution();
                System.out.println("作業執行中的狀態"+jobExecution.getStatus());

                return RepeatStatus.FINISHED; //通知執行完了
            }
        };
    }

    @Bean
    public JobStateListener jobStateListener(){
        return new JobStateListener();
    }

    @Bean
    public Step step1(){

        return stepBuilderFactory.get("step1").tasklet(tasklet())
                .build();
    }



    @Bean
    public Job job(){

        return jobBuilderFactory.get("job-state-anno-listener-job4")
                .start(step1())
                //.listener(jobStateListener())
                //.listener(new JobStateAnnoListener())
                .listener(JobListenerFactoryBean.getListener(new JobStateAnnoListener()))
                .build();
    }
    public static void main(String[] args) {
        SpringApplication.run(StatusListenerJob.class,args);
    }

}
