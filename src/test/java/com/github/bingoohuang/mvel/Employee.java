package com.github.bingoohuang.mvel;

import lombok.Data;

@Data
public class Employee {
    private String firstName;
    private String lastName;
    private int age;

    public Employee(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
