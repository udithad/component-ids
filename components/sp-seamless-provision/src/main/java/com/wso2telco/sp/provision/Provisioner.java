package com.wso2telco.sp.provision;

public abstract class Provisioner<K, T> {

    public abstract K provisionServiceProvider(T t);

}
