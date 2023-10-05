package com.example.springbatch._25_itemreader_db_page;



import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//讀user.txt文件封裝到user對象中並打印
@EnableBatchProcessing
@SpringBootApplication
public class PageDBReaderJob {

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
    public UserRowMapper userRowMapper (){
        return  new UserRowMapper();
    }

    //分頁查詢邏輯
    @Bean
    public PagingQueryProvider pagingQueryProvider () throws Exception {
        SqlPagingQueryProviderFactoryBean factoryBean = new SqlPagingQueryProviderFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setSelectClause("select *");//查詢語句
        factoryBean.setFromClause("from user");//查詢表
        factoryBean.setWhereClause("where age > :age"); //: age表示佔位符
        factoryBean.setSortKey("id");//依照id排序

        return factoryBean.getObject();
    }


    //select * from user where .... limit ?,pageSize
    //使用JDBC方法1
    @Bean
    public JdbcPagingItemReader<User> itemReader() throws Exception {

        Map<String,Object> map = new HashMap<>();
        map.put("age",16);

        return new JdbcPagingItemReaderBuilder<User>()
                .name("userItemReader")
                .dataSource(dataSource)
                .rowMapper(userRowMapper())
                .queryProvider(pagingQueryProvider())//分頁邏輯
                //sql條件值對應 factoryBean.setWhereClause("where age > :age"); 裡的 :age 所以 age會是16
                .parameterValues(map)
                .pageSize(10)//每頁顯示幾筆
                .build();

    }




    @Bean
    public Step step1() throws Exception {
        return stepBuilderFactory.get("step1")
                .<User, User>chunk(1)//一次讀多少數據
                .reader(itemReader())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public Job job() throws Exception {

        return jobBuilderFactory.get("page-db-reader-job1")
                .start(step1())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(PageDBReaderJob.class,args);
    }



}
