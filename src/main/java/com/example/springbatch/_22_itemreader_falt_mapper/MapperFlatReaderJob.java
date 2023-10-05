package com.example.springbatch._22_itemreader_falt_mapper;


import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
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
public class MapperFlatReaderJob {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    //創造一個step對象執行的任務
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    //job->step->chunk->reader->writer

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
    public UserFieldMapper userFieldMapper(){
        return new UserFieldMapper();
    }


    // 1  d   18
    //id name age
    @Bean
    public FlatFileItemReader<User>  itemReader(){
        return new FlatFileItemReaderBuilder<User>()
                .name("userItemReader")//對構造器取名子
                //設置文件
                .resource(new ClassPathResource("user2.txt"))
                //解析數據--指定解析器使用#號分割--默認是,號
                .delimited().delimiter("#")
                //按照#號，數據怎麼命名
                .names("id","name","age","province","city","area")
                //封裝數據 將讀取的數據封裝到對象:User 對象
                //.targetType(User.class) 自動封裝
                .fieldSetMapper(userFieldMapper()) //字段映射
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

        return jobBuilderFactory.get("mapper-flat-reader-job")
                .start(step1())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(MapperFlatReaderJob.class,args);
    }



}
