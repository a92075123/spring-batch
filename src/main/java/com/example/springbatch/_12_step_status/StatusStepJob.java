package com.example.springbatch._12_step_status;

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
public class StatusStepJob {

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

        return jobBuilderFactory.get("status-step-job")
                .start(firstStep())
                //.on("FAILED").end()  //表示將當前報錯的步驟直接正常結束--COMPLETED
                //.on("FAILED").fail() //表示將當前報錯的步驟直接失敗結束--FAILED
                .on("FAILED").stopAndRestart(successStep())  //表示將當前報錯的步驟直接轉成停止狀態--STOPPED，裡面的參數表示後續要重啟時，從successStep()位置開始
                .from(firstStep()).on("*").to(successStep())
                .end()
                .incrementer(new RunIdIncrementer())
                .build();
    }
    public static void main(String[] args) {

        SpringApplication.run(StatusStepJob.class,args);

    }
}
