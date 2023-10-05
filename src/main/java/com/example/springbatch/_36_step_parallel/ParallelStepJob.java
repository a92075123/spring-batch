package com.example.springbatch._36_step_parallel;

import com.example.springbatch._23_itemreader_flat_json.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import java.util.List;

@EnableBatchProcessing
@SpringBootApplication
public class ParallelStepJob {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    //創造一個step對象執行的任務
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public JsonItemReader<User> jsonItemReader(){

        ObjectMapper objectMapper = new ObjectMapper();
        //參數:讀取json格式文件轉換成具體的對象類型:User.class
        JacksonJsonObjectReader<User> userJacksonJsonObjectReader = new JacksonJsonObjectReader<>(User.class);
        userJacksonJsonObjectReader.setMapper(objectMapper);

        return new JsonItemReaderBuilder<User>()
                .name("userJsonItemReader")
                .resource(new ClassPathResource("user-parellel.json"))
                .jsonObjectReader(userJacksonJsonObjectReader)
                .build();

    }

    @Bean
    public FlatFileItemReader<User> flatItemReader(){
        return new FlatFileItemReaderBuilder<User>()
                .name("userItemReader")//對構造器取名子
                //設置文件
                .resource(new ClassPathResource("user-parellel.txt"))
                //解析數據--指定解析器使用#號分割--默認是,號
                .delimited().delimiter("#")
                //按照#號，數據怎麼命名
                .names("id","name","age")
                .targetType(User.class)
                .build();
    }

    @Bean
    public ItemWriter<User> itemWriter(){
        return new ItemWriter<User>() {
            @Override
            public void write(List<? extends User> list) throws Exception {
                list.forEach(System.out::println);
            }
        };
    }
    @Bean
    public Step flatStep(){
        return stepBuilderFactory.get("flatStep")
                .<User,User>chunk(2)
                .reader(flatItemReader())
                .writer(itemWriter())
                .build();
    }
    @Bean
    public Step jsonStep(){
        return stepBuilderFactory.get("flatStep")
                .<User,User>chunk(2)
                .reader(jsonItemReader())
                .writer(itemWriter())
                .build();
    }


    @Bean
    public Job job(){

        //並行1
        Flow flowParallel1 = new FlowBuilder<Flow>("flowParallel1").start(flatStep())
                .build();
        //並行2
        Flow flowParallel2 = new FlowBuilder<Flow>("flowParallel2")
                .start(jsonStep())
                .split(new SimpleAsyncTaskExecutor())//開啟線程執行步驟
                .add(flowParallel1)//並行1跟並行2同時啟動
                .build();

        return jobBuilderFactory.get("parallel-step-job4")
                .start(flowParallel2)
                .end()
                .build();

    }

    public static void main(String[] args) {
        SpringApplication.run(ParallelStepJob.class,args);
    }


}
