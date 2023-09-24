package com.example.springbatch._13_flow_step;

import com.example.springbatch._11_step_condition.ConditionStepJob;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
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
public class FlowStepJob {

    @Autowired
    private JobLauncher jobLauncher;
    //創造step對象的工廠
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    //創造一個step對象執行的任務
    @Autowired
    private StepBuilderFactory stepBuilderFactory;


  @Bean
    public Tasklet taskletA() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {


                System.out.println("---------taskletA----------");

                return RepeatStatus.FINISHED;
            }
        };
    }

    @Bean
    public Tasklet taskletB1() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {


                System.out.println("----stepB-----taskletB1----------");

                return RepeatStatus.FINISHED;
            }
        };
    }

    @Bean
    public Tasklet taskletB2() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {


                System.out.println("----stepB-----taskletB2----------");

                return RepeatStatus.FINISHED;
            }
        };
    }
    @Bean
    public Tasklet taskletB3() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {


                System.out.println("----stepB-----taskletB3----------");

                return RepeatStatus.FINISHED;
            }
        };
    }

    @Bean
    public Tasklet taskletC() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {


                System.out.println("---------taskletC----------");

                return RepeatStatus.FINISHED;
            }
        };
    }


    @Bean
    public Step stepA(){

        return stepBuilderFactory.get("stepA")
                .tasklet(taskletA())
                .build();
    }
    public Step stepB1(){

        return stepBuilderFactory.get("stepB1")
                .tasklet(taskletB1())
                .build();
    }
    public Step stepB2(){

        return stepBuilderFactory.get("stepB2")
                .tasklet(taskletB2())
                .build();
    }
    public Step stepB3(){

        return stepBuilderFactory.get("stepB3")
                .tasklet(taskletB3())
                .build();
    }

    //創造一個流式步驟

    public Flow flowB(){
      return new FlowBuilder<Flow>("flowB")
              .start(stepB1())
              .next(stepB2())
              .next(stepB3())
              .build();
    }
    //job 沒有flowStep步驟的方法，必須使用step進行封裝在執行
    @Bean
    public Step stepB(){

        return stepBuilderFactory.get("stepB")
                .flow(flowB())
                .build();
    }
    @Bean
    public Step stepC(){

        return stepBuilderFactory.get("stepC")
                .tasklet(taskletC())
                .build();
    }






    //如果firstStep 執行成功:下一步執行successStep 否則是failStep
    @Bean
    public Job job(){


        return jobBuilderFactory.get("flow-step-job")
                .start(stepA())
                .next(stepB())
                .next(stepC())
                .incrementer(new RunIdIncrementer())
                .build();

    }
    public static void main(String[] args) {

        SpringApplication.run(FlowStepJob.class,args);

    }
}
