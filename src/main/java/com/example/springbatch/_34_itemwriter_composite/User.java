package com.example.springbatch._34_itemwriter_composite;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
public class User {
    private Long id;
    @NotBlank(message = "名字不能為空串")
    private String name;
    private int age;
}
