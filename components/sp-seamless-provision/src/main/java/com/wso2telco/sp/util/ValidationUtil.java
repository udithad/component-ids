package com.wso2telco.sp.util;

import com.wso2telco.sp.discovery.exception.DicoveryException;

public class ValidationUtil {

    public static boolean validateInuts(String sector, String clientId) throws DicoveryException {
        if (!validateClientId(clientId)) {
            throw new DicoveryException("Empty clientId found", true);
        }
        if (!validateSectorId(sector)) {
            throw new DicoveryException("Empty sector found", true);
        }
        return true;
    }

    private static boolean validateSectorId(String sector) {
        boolean isValied = true;
        if (sector == null || sector.isEmpty()) {
            isValied = false;
        }
        return isValied;
    }

    private static boolean validateClientId(String clientId) {
        boolean isValied = true;
        if (clientId == null || clientId.isEmpty()) {
            isValied = false;
        }
        return isValied;
    }

    
}
