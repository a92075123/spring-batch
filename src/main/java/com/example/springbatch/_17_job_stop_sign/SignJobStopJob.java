package com.example.springbatch._17_job_stop_sign;


import com.example.springbatch._16_job_stop.StopStepListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableBatchProcessing
public class SignJobStopJob {

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

                //設置不滿足條件:readCount!=totalCount設置停止標記
                if(ResourceCount.readCount!=ResourceCount.totalCount){
                    //停止標記
                    chunkContext.getStepContext().getStepExecution().setTerminateOnly();
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
                System.err.println("readCount:"+ ResourceCount.readCount+", totalCount:"+ ResourceCount.totalCount);


                return RepeatStatus.FINISHED; //會一直跑下去
            }
        };
    }



    @Bean
    public Step step1(){

        return stepBuilderFactory.get("step1")
                .tasklet(tasklet1())
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

        return jobBuilderFactory.get("sign-step-stop-job3")
                .start(step1())
                .next(step2())
                .build();
    }
    public static void main(String[] args) {

        SpringApplication.run(SignJobStopJob.class,args);

    }
}
