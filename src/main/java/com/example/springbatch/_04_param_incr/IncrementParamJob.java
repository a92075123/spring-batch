package com.example.springbatch._04_param_incr;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
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
public class IncrementParamJob {
    //創建Job對象的工廠
    @Autowired
    private JobLauncher jobLauncher;
    //創造step對象的工廠
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    //創造一個step對象執行的任務
    @Autowired
    private StepBuilderFactory stepBuilderFactory;


    //StepScope延遲操作，正常是一起創建出來，當用了這個註解，他不會先創建他，而是等到要使用這個方法(tasklet)的時候才創建這個Bean
    @StepScope
    @Bean
    public Tasklet tasklet() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                //要執行邏輯--step步驟執行邏輯
                //第一種方法 使用chunkContext
                Map<String, Object> jobParameters = chunkContext.getStepContext().getJobParameters();
                System.out.println("param---daily:"+jobParameters.get("daily"));
                return RepeatStatus.FINISHED;

            }
        };
    }
    @Bean
    public DailyTimestampParamIncrementer dailyTimestampParamIncrementer (){
        return  new DailyTimestampParamIncrementer();
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
    return jobBuilderFactory.get("incr-params-job")
            .start(step1())
            //.incrementer(new RunIdIncrementer())自動增長run.id
            .incrementer(dailyTimestampParamIncrementer())//時間戳的自增
            .build();
}
    public static void main(String[] args) {
        SpringApplication.run(IncrementParamJob.class,args);
    }

}
