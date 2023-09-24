package com.example.springbatch._05_listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;


/*
任務狀態監聽器，使用註解的方式
 */
public class JobStateAnnoListener {

    @BeforeJob
    public void beforeJob(JobExecution jobExecution){
        System.err.println("任務執行前的狀態"+jobExecution.getStatus());
    }
    @AfterJob
    public void afterJob(JobExecution jobExecution){
        System.err.println("任務執行後的狀態"+jobExecution.getStatus());
    }

}
