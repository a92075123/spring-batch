package com.example.springbatch._30_itemprocessor_customize;

import org.springframework.batch.item.ItemProcessor;
//自訂義解析器
public class CustomizeItemProcessor implements ItemProcessor<User,User> {

    @Override
    public User process(User user) throws Exception {

        //將id為偶數數據獲取，其他不要
        return user.getId()%2==0 ? user:null;
    }
}
