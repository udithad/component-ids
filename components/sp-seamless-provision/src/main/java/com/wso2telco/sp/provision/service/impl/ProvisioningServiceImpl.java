package com.wso2telco.sp.provision.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wso2telco.core.spprovisionservice.external.admin.services.AouthAdminService;
import com.wso2telco.core.spprovisionservice.external.admin.services.impl.AouthAdminServiceImpl;
import com.wso2telco.sp.provision.service.ProvisioningService;

public class ProvisioningServiceImpl implements ProvisioningService<Object, Object> {

    private static Log log = LogFactory.getLog(ProvisioningServiceImpl.class);

    private AouthAdminService authService = null;

    public ProvisioningServiceImpl() {
        authService = new AouthAdminServiceImpl();
    }

    @Override
    public Object provisionServiceProvider(Object t) {
        // TODO Auto-generated method stub

        log.info("=========================================================");
        log.info("provisionServiceProvider");

        log.info(authService.testMethod(""));
        log.info("=========================================================");
        return null;
    }

}
