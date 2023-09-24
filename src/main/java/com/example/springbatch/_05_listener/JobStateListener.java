package com.example.springbatch._05_listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

/*
自訂任務狀態監聽器
作用:用於紀錄任務執行前與執行後的狀態
 */
public class JobStateListener implements JobExecutionListener {
    //任務執行前
    @Override
    public void beforeJob(JobExecution jobExecution) {
       System.err.println("任務執行前的狀態"+jobExecution.getStatus());
    }
    //任務執行後
    @Override
    public void afterJob(JobExecution jobExecution) {
        System.err.println("任務執行後的狀態"+jobExecution.getStatus());
    }
}
