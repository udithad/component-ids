package com.wso2telco.sp.builder;

public class ServiceProviderBuilder<K, T> {

    public K buildOauthDataStructure(T t) {
        // implement registerOAuthApplicationData

        return null;
    }

    public K buildSpApplicationDataStructure(T t) {

        // implement createSpApplication
        // implement getSpApplicationData
        // implement updateSpApplication

        return null;
    }

    public K reBuildOauthDataStructure(T t, Object oldSpOauthProvider) {

        // implement removeOAuthApplicationData
        // implement registerOAuthApplicationData
        return null;
    }

    public K revokeSpApplication(T t) {
        // implement removeOAuthApplicationData
        // implement deleteSpApplication
        return null;
    }

}
