package com.lhk.service;

import java.io.Serializable;

public class User implements Serializable {
    private String name;
    private String desc;
    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", age=" + age +
                '}';
    }
}

