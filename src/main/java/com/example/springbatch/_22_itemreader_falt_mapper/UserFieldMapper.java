package com.example.springbatch._22_itemreader_falt_mapper;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

//將解析出來的數據進行封裝自定義封裝
public class UserFieldMapper implements FieldSetMapper<User> {
    @Override
    public User mapFieldSet(FieldSet fieldSet) throws BindException {

        User user = new User();

        user.setId(fieldSet.readLong("id"));
        user.setName(fieldSet.readString("name"));
        user.setAge(fieldSet.readInt("age"));
        String address = ""+ fieldSet.readString("province") +fieldSet.readString("area") +fieldSet.readString("city");

        user.setAddress(address);


        return user;
    }
}
