package com.example.springbatch._09_step_listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
//自訂step任務監聽器
public class MyStepListener implements StepExecutionListener {
    @Override
    public void beforeStep(StepExecution stepExecution) {
        System.out.println("------before-----");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        System.out.println("------afterStep-----");
        return stepExecution.getExitStatus();//必要
    }
}
