package com.example.springbatch._27_itemprocessor_adapter;


import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.adapter.ItemProcessorAdapter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

import java.util.List;


//讀user.txt文件封裝到user對象中並打印
@EnableBatchProcessing
@SpringBootApplication
public class AdapterProcessorJob {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    //創造一個step對象執行的任務
    @Autowired
    private StepBuilderFactory stepBuilderFactory;



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
    public FlatFileItemReader<User>  itemReader(){
        return new FlatFileItemReaderBuilder<User>()
                .name("userItemReader")//對構造器取名子
                //設置文件
                .resource(new ClassPathResource("user-adapter.txt"))
                //解析數據--指定解析器使用#號分割--默認是,號
                .delimited().delimiter("#")
                //按照#號，數據怎麼命名
                .names("id","name","age")
                //封裝數據 將讀取的數據封裝到對象:User 對象
                .targetType(User.class)
                .build();
    }

    //已經定義好的 用戶名轉換類
    //當前需求:使用邏輯轉換處理器調用該類UserServiceImpl的toUppeCase 實現用戶名轉換成大寫
    @Bean
    public  UserServiceImpl userService(){
        return new UserServiceImpl();
    }


    //需要有現成類做轉換邏輯: 將姓名轉換成大寫
    //處理邏輯
    @Bean
    public ItemProcessorAdapter<User,User> itemProcessorAdapter(){
        ItemProcessorAdapter<User,User> adapter = new ItemProcessorAdapter<>();
        adapter.setTargetObject(userService());//丟入要執行轉換邏輯的類 ex:UserServiceImpl
        adapter.setTargetMethod("toUppeCase");//丟入執行轉換邏輯的類裡面的方法
        return adapter;
    }


    @Bean
    public Step step1(){
        return stepBuilderFactory.get("step1")
                .<User, User>chunk(1)//一次讀多少數據
                .reader(itemReader())
                .processor(itemProcessorAdapter())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public Job job(){

        return jobBuilderFactory.get("adapter-processor-job")
                .start(step1())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(AdapterProcessorJob.class,args);
    }



}
