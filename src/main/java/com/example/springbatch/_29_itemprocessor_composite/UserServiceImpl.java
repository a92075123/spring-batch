package com.example.springbatch._29_itemprocessor_composite;



public class UserServiceImpl {
    public User toUppeCase(User user){
        user.setName(user.getName().toUpperCase());
        return user;
    }
}
