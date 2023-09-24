package com.example.springbatch._03_param_validator;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.Map;

@SpringBootApplication
@EnableBatchProcessing
public class ParamValidatorJob {
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

    @Bean
    public Tasklet tasklet() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                //要執行邏輯--step步驟執行邏輯
                //第一種方法 使用chunkContext
                Map<String, Object> jobParameters = chunkContext.getStepContext().getJobParameters();
                System.out.println("param--必填--name:" + jobParameters.get("name"));
                System.out.println("param--可選--age:" + jobParameters.get("age"));
                return RepeatStatus.FINISHED;
            }
        };
    }

    @Bean
    public Step step1() {
        //tasklet  執行step的邏輯，類似屬於thread()--->可以執行runable
        return stepBuilderFactory.get("step1").tasklet(tasklet())
                .build();
    }


    //自訂的參數檢查器
    @Bean
    public NameParamValidator nameParamValidator() {
        return new NameParamValidator();
    }

    //默認參數檢查器
    @Bean
    public  DefaultJobParametersValidator defaultApplicationArguments(){
        DefaultJobParametersValidator validator = new DefaultJobParametersValidator();
        //必填的參數
        validator.setRequiredKeys(new String[]{"name"});//必須要name參數
        //可選的參數
        validator.setOptionalKeys(new String[]{"age"});//age是可選

        return validator;
    }
    //組合參數解析器
    @Bean
    public CompositeJobParametersValidator compositeJobParametersValidator() throws Exception {
        CompositeJobParametersValidator validator = new CompositeJobParametersValidator();
        validator.setValidators(Arrays.asList(nameParamValidator(),defaultApplicationArguments()));

        validator.afterPropertiesSet();

        return validator;
    }


//    @Bean
//    public Job job() {
//        return jobBuilderFactory.get("name-param-validate-value-job")
//                .start(step1())
//                .validator(nameParamValidator())//指定參數檢測器
//                .build();
//    }

//    @Bean
//    public Job job() {
//        return jobBuilderFactory.get("default-name-param-validate-job")
//                .start(step1())
//                .validator(defaultApplicationArguments())//指定參數檢測器
//                .build();
//    }

    @Bean
    public Job job() throws Exception {
        return jobBuilderFactory.get("composite-param-validate-job")
                .start(step1())
                .validator(compositeJobParametersValidator())//指定參數檢測器
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(ParamValidatorJob.class, args);
    }

}
