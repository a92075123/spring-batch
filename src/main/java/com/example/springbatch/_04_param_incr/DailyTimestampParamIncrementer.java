package com.example.springbatch._04_param_incr;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersIncrementer;

import java.util.Date;

//以時間為參數增量器
public class DailyTimestampParamIncrementer implements JobParametersIncrementer {
    @Override
    public JobParameters getNext(JobParameters jobParameters) {
        return new JobParametersBuilder(jobParameters)
                .addLong("daily",new Date().getTime())
                .toJobParameters();
    }
}
