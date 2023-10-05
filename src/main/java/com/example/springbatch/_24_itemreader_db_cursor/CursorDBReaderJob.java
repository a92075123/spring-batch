package com.example.springbatch._24_itemreader_db_cursor;






import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;

import javax.sql.DataSource;
import java.util.List;


//讀user.txt文件封裝到user對象中並打印
@EnableBatchProcessing
@SpringBootApplication
public class CursorDBReaderJob {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    //創造一個step對象執行的任務
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private DataSource dataSource;

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

    //將數據與對象一一映射
    @Bean
    public  UserRowMapper userRowMapper (){
        return  new UserRowMapper();
    }


    //使用JDBC方法1
    @Bean
    public JdbcCursorItemReader<User> itemReader(){
        return new JdbcCursorItemReaderBuilder<User>()
                .name("userItemReader")
                //連接數據庫，spring容器自己實現
                .dataSource(dataSource)
                //執行sql查詢數據，將返回的數據以游標形式一條一條讀
                .sql("select * from user where age < ? and age > ?")
                //拼接參數 30,15分別代表第一個問號跟第二個問號
                .preparedStatementSetter(new ArgumentPreparedStatementSetter(new Object[]{30,15}))
                //資料庫讀出的數據跟User對象屬性一一映射
                .rowMapper(userRowMapper())
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

        return jobBuilderFactory.get("cursor-db-reader-job3")
                .start(step1())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(CursorDBReaderJob.class,args);
    }



}
