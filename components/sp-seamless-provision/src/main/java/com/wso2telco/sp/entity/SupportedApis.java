package com.wso2telco.sp.entity;

public class SupportedApis {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ClassPojo [name = " + name + "]";
    }
}