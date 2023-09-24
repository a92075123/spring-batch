package com.example.springbatch._03_param_validator;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.util.StringUtils;

/*
進行name參數驗證
當name為空或是null拋出異常
 */
public class NameParamValidator implements JobParametersValidator {
    @Override
    public void validate(JobParameters jobParameters) throws JobParametersInvalidException {

        String name = jobParameters.getString("name");
        //StringUtils.hasText檢查是否有值
        if(!StringUtils.hasText(name)){
            throw new JobParametersInvalidException("name 參數值不能為null或者空");
        }
    }
}
