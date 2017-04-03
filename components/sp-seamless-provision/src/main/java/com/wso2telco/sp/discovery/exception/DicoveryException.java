package com.wso2telco.sp.discovery.exception;

public class DicoveryException extends Exception {

    private String responseCode = null;
    private boolean isSystemError = false;

    public DicoveryException() {

    }

    public DicoveryException(String messageCode, boolean isSystemError) {
        super(messageCode);
        this.isSystemError = isSystemError;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public boolean isSystemError() {
        return isSystemError;
    }

}
