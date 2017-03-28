package com.wso2telco.sp.provision;

public class ServiceProviderProvisionFactory<K, T> {

    private Provisioner<Object, Object> provisioner;

    public Provisioner<Object, Object> getProvisioner(T t) {

        // implement the remote and local provisioner seletion by type
        return null;
    }

}
