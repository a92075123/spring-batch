package com.example.springbatch._16_job_stop;

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
public class ListenerJobStopJob {

    @Autowired
    private JobLauncher jobLauncher;
    //創造step對象的工廠
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    //創造一個step對象執行的任務
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    //模擬從數據庫中查詢數據

    private  int readCountDB = 100 ;


  @Bean
    public Tasklet tasklet1() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                System.out.println("---------step1----------");
                for (int i = 0; i <readCountDB ; i++) {
                    ResourceCount.readCount++;
                }
                return RepeatStatus.FINISHED; //會一直跑下去
            }
        };
    }
    @Bean
    public Tasklet tasklet2() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {


                System.err.println("---------step2執行了----------");
                System.err.println("readCount:"+ResourceCount.readCount+", totalCount:"+ResourceCount.totalCount);


                return RepeatStatus.FINISHED; //會一直跑下去
            }
        };
    }

@Bean
public  StopStepListener stopStepListener(){
      return new StopStepListener();
}

    @Bean
    public Step step1(){

        return stepBuilderFactory.get("step1")
                .tasklet(tasklet1())
                .listener(stopStepListener())
                .allowStartIfComplete(true)//允許step重新執行
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

        return jobBuilderFactory.get("step--job")
                .start(step1())
                //當step1返回的是STOPPED狀態馬，馬上結束流程，設置流程狀態為:STOPPED，並設置重啟，從step1開始執行
                .on("STOPPED").stopAndRestart(step1())
                //如果step1執行結果不是STOPPED而是其他狀態，表示滿足判斷條件，然後執行step2
                .from(step1()).on("*").to(step2())
                .end()
                .build();
    }
    public static void main(String[] args) {

        SpringApplication.run(ListenerJobStopJob.class,args);

    }
}
