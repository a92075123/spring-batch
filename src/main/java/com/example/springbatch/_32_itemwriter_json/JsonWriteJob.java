package com.example.springbatch._32_itemwriter_json;



import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.PathResource;


//讀user.txt文件封裝到user對象中並打印
@EnableBatchProcessing
@SpringBootApplication
public class JsonWriteJob {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    //創造一個step對象執行的任務
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    //job->step->chunk->reader->writer


    //json對象調度器
    @Bean
    public JacksonJsonObjectMarshaller<User> jsonObjectMarshaller(){
        return new JacksonJsonObjectMarshaller<>();
    }

    //輸出到outUser.txt文件
    @Bean
    public JsonFileItemWriter<User> itemWriter(){
        return new JsonFileItemWriterBuilder<User>()
                .name("userJsonItemWriter")
                .resource(new PathResource("D:/outUser.json"))
                //json對象調度器--將user對象緩存json格式，輸出文檔中
                .jsonObjectMarshaller(jsonObjectMarshaller())
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

        return jobBuilderFactory.get("json-writer-job")
                .start(step1())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(JsonWriteJob.class,args);
    }



}
