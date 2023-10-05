package com.example.springbatch._31_itemwriter_flat;


import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.PathResource;

import java.util.List;


//讀user.txt文件封裝到user對象中並打印
@EnableBatchProcessing
@SpringBootApplication
public class FlatWriteJob {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    //創造一個step對象執行的任務
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    //job->step->chunk->reader->writer

//    @Bean
//    public ItemWriter<User> itemWriter(){
//        return new ItemWriter<User>() {
//            @Override
//            public void write(List<? extends User> list) throws Exception {
//                list.forEach(System.out::println);
//            }
//        };
//    }

    @Bean
    public FlatFileItemWriter<User> itemWriter(){
      return   new FlatFileItemWriterBuilder<User>()
                .name("userItemWriter")
                .resource(new PathResource("D:/outUser.txt"))//輸出的位置
                .formatted()//要進行格式輸出
                .format("id:%s,姓名:%s,年齡:%s")//輸出數據的格式
                .names("id","name","age")//根據itemReader的.names給於每個%相對應的值
                //.shouldDeleteIfEmpty(true) 如果數據為空，創建文件會直接被刪除
                //.shouldDeleteIfExists(true) 輸出文件如果存在，則刪除
                //.append(true)如果輸出文件存在，不刪除，直接加到現有文件中
                .build();
    }


    @Bean
    public FlatFileItemReader<User>  itemReader(){
        return new FlatFileItemReaderBuilder<User>()
                .name("userItemReader")//對構造器取名子
                //設置文件
                .resource(new ClassPathResource("user.txt"))
                //解析數據--指定解析器使用#號分割--默認是,號
                .delimited().delimiter("#")
                //按照#號，數據怎麼命名
                .names("id","name","age")
                //封裝數據 將讀取的數據封裝到對象:User 對象
                .targetType(User.class)
                .build();
    }

    @Bean
    public Step step1(){
        return stepBuilderFactory.get("step1")
                .<User, User>chunk(1)//一次讀多少數據
                .reader(itemReader())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public Job job(){

        return jobBuilderFactory.get("flat-writer-job")
                .start(step1())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(FlatWriteJob.class,args);
    }



}
