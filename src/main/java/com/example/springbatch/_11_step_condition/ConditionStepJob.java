package com.example.springbatch._11_step_condition;

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
public class ConditionStepJob {

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
                throw new RuntimeException("假裝失敗了");
//                return RepeatStatus.FINISHED; //會一直跑下去
            }
        };
    }
    @Bean
    public Tasklet successTasklet() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {


                System.out.println("---------successTasklet----------");

                return RepeatStatus.FINISHED; //會一直跑下去
            }
        };
    }

    @Bean
    public Tasklet failTasklet() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {


                System.out.println("---------failTasklet----------");


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
    public Step successStep(){

        return stepBuilderFactory.get("successStep")
                .tasklet(successTasklet())
                .build();
    }
    public Step failStep(){

        return stepBuilderFactory.get("failStep")
                .tasklet(failTasklet())
                .build();
    }

    //如果firstStep 執行成功:下一步執行successStep 否則是failStep
    @Bean
    public Job job(){
      /*
      假設firstStep() 返回狀態變量是:a
      if(a.equals=="FAILED"))
      .to(failStep())
      else
      .to(successStep())

       */

        return jobBuilderFactory.get("step-multi-job")
                //達成xxx條件執行後續的邏輯，滿足firstStep執行狀態為失敗的時候
                .start(firstStep()).on("FAILED").to(failStep())
                //* 表示else邏輯
                .from(firstStep()).on("*").to(successStep()).end()
                .incrementer(new RunIdIncrementer())
                .build();
    }
    public static void main(String[] args) {

        SpringApplication.run(ConditionStepJob.class,args);

    }
}
