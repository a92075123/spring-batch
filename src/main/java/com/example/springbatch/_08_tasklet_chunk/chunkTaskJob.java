package com.example.springbatch._08_tasklet_chunk;

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
import org.springframework.batch.item.*;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

/*
只有數據為null或空的時候，執行才會停下來，不然會一直死循環
 */
@SpringBootApplication
@EnableBatchProcessing
public class chunkTaskJob {

    @Autowired
    private JobLauncher jobLauncher;
    //創造step對象的工廠
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    //創造一個step對象執行的任務
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    int timer=10;
    @Bean
    //讀操作
    public ItemReader<String> itemReader() {
        return new ItemReader<String>() {
            @Override
            public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
               if(timer>0){
                   System.out.println("-----read-----");
                   return "read-ret" + timer--;
               }else {
                   return null;
               }
            }
        };
    }

    //數據處理操作
    @Bean
    public ItemProcessor<String,String> itemProcessor(){
        return  new ItemProcessor<String,String>() {
            @Override
            public String process(String o) throws Exception {

                System.out.println("-----process----"+o);

                return "process-ret"+o;
            }
        };
    }

    //寫操作
    @Bean
    public ItemWriter<String> itemWriter(){
        return new ItemWriter<String>() {
            @Override
            public void write(List<? extends String> list) throws Exception {
                System.out.println(list);
            }
        };
    }







    @Bean
    public Step step1(){
        return stepBuilderFactory.get("step1")
                .<String,String>chunk(3)
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .build();
    }


    @Bean
    public Job job(){
        return jobBuilderFactory.get("simple-tasklet-job")
                .start(step1())
                .incrementer(new RunIdIncrementer())
                .build();
    }
    public static void main(String[] args) {

        SpringApplication.run(chunkTaskJob.class,args);

    }
}
