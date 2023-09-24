package com.example.springbatch._11_step_condition_decider;

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
public class CustomizeStatusStepJob {

    @Autowired
    private JobLauncher jobLauncher;
    //創造step對象的工廠
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    //創造一個step對象執行的任務
    @Autowired
    private StepBuilderFactory stepBuilderFactory;


  @Bean
    public Tasklet firstTasklet() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {


                System.out.println("---------firstTasklet----------");

               return RepeatStatus.FINISHED;
            }
        };
    }
    @Bean
    public Tasklet taskletA() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {


                System.out.println("---------taskletA----------");

                return RepeatStatus.FINISHED; //會一直跑下去
            }
        };
    }

    @Bean
    public Tasklet taskletB() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {


                System.out.println("---------taskletB----------");


                return RepeatStatus.FINISHED; //會一直跑下去
            }
        };
    }
    @Bean
    public Tasklet taskletDefault() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {


                System.out.println("---------taskletDefault----------");


                return RepeatStatus.FINISHED; //會一直跑下去
            }
        };
    }

    @Bean
    public Step firstStep(){

        return stepBuilderFactory.get("firstStep")
                .tasklet(firstTasklet())
                .build();
    }

    @Bean
    public Step stepA(){

        return stepBuilderFactory.get("StepA")
                .tasklet(taskletA())
                .build();
    }
    public Step stepB(){

        return stepBuilderFactory.get("StepB")
                .tasklet(taskletB())
                .build();
    }

    public Step stepDefault(){

        return stepBuilderFactory.get("StepDefault")
                .tasklet(taskletDefault())
                .build();
    }

    //如果firstStep 執行成功:下一步執行successStep 否則是failStep

    @Bean
    public  MyStatusDecider statusDecider(){
      return new MyStatusDecider();
    }

    /*
    if("A".eqauls(statusDecider()){
    .to(stepA())
    }else if("B".eqauls(statusDecider()){
    .to(stepB())
    }else{
    .to(stepDefault()
    )
    }
     */


    @Bean
    public Job job(){

        return jobBuilderFactory.get("step-multi-job")
                .start(firstStep())
                .next(statusDecider())
                .from(statusDecider()).on("A").to(stepA())
                .from(statusDecider()).on("B").to(stepB())
                .from(statusDecider()).on("*").to(stepDefault())
                .end()
                .incrementer(new RunIdIncrementer())
                .build();
    }
    public static void main(String[] args) {

        SpringApplication.run(CustomizeStatusStepJob.class,args);

    }
}
