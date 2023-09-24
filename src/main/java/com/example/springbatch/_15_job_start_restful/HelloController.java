//package com.example.springbatch._15_job_start_restful;
//
//import org.springframework.batch.core.*;
//import org.springframework.batch.core.explore.JobExplorer;
//import org.springframework.batch.core.launch.JobLauncher;
//import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
//import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
//import org.springframework.batch.core.repository.JobRestartException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//public class HelloController {
//
//    @Autowired
//    private JobLauncher launcher;
//
//    @Autowired
//    private Job job;
//
//    @Autowired
//    private JobExplorer explorer; //job相關對象的資料紀錄
//
//    //http://localhost:8080/job/start
//    @GetMapping("/job/start")
//    public ExitStatus startJob(String name) throws Exception {
//
//        //run.id自增的前提，先拿到之前的jobparameter中的run.id才能進行自增
//        //也就是說，當前請求想要讓run.id 自增，需要獲取之前的jobparameter才能加1
//
//        System.out.println("------------");
//        JobParameters jobParameter = new JobParametersBuilder(explorer)
//                .getNextJobParameters(job)
//                .addString("name",name)
//                .toJobParameters();
//
//        //啟動job作業
//        JobExecution jobExecution = launcher.run(job,jobParameter);
//
//        return jobExecution.getExitStatus();
//    }
//
//}
