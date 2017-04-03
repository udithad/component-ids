package com.wso2telco.sp.discovery.exception;

public class DiscoveryValidationException extends Exception {

    public DiscoveryValidationException() {

    }

    public DiscoveryValidationException(String messageCode) {
        super(messageCode);
    }

}
