/**
 * 
 */
package com.wso2telco.sp.provision.service;

public interface ProvisioningService<K, T> {

    public K provisionServiceProvider(T t);

}
