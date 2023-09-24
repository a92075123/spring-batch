package com.example.springbatch._06_context;

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
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableBatchProcessing
public class ExecutionContextJob {

    @Autowired
    private JobLauncher jobLauncher;
    //創造step對象的工廠
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

                //步驟
                //可以獲取共享的數據，但是不允許修改
                //Map<String, Object> stepExecutionContext = chunkContext.getStepContext().getStepExecutionContext();

                //通過執行上下文對象獲取跟設置參數
                ExecutionContext stepEC = chunkContext.getStepContext().getStepExecution().getExecutionContext();
                stepEC.put("key-step1-step","value-step1-step");

                System.out.println("---------1----------");
                //任務
                ExecutionContext jobEC = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
                jobEC.put("key-step1-job","value-step1-job");

                return RepeatStatus.FINISHED; //通知執行完了
            }
        };
    }
   @Bean
    public Tasklet tasklet2(){
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                //步驟
                ExecutionContext stepEC = chunkContext.getStepContext().getStepExecution().getExecutionContext();

                System.err.println(stepEC.get("key-step1-step"));
                System.out.println("-----------2-------------");
                //任務
                ExecutionContext jobEC = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
                System.err.println(jobEC.get("key-step1-job"));
                return RepeatStatus.FINISHED; //通知執行完了
            }
        };
    }

    @Bean
    public Step step1(){

        return stepBuilderFactory.get("step1").tasklet(tasklet1())
                .build();
    }
    @Bean
    public Step step2(){

        return stepBuilderFactory.get("step2").tasklet(tasklet2())
                .build();
    }


    @Bean
    public Job job(){

        return jobBuilderFactory.get("api-execution-context-job")
                .start(step1())
                .next(step2())
                .incrementer(new RunIdIncrementer())
                .build();
    }
    public static void main(String[] args) {

        SpringApplication.run(ExecutionContextJob.class,args);

    }
}
