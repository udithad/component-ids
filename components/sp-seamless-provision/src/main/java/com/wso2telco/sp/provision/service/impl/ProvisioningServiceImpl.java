package com.wso2telco.sp.provision.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wso2telco.core.spprovisionservice.external.admin.service.OauthAdminService;
import com.wso2telco.core.spprovisionservice.external.admin.service.impl.OauthAdminServiceImpl;
import com.wso2telco.sp.discovery.service.DiscoveryService;
import com.wso2telco.sp.discovery.service.impl.DiscoveryServiceImpl;
import com.wso2telco.sp.provision.service.ProvisioningService;

public class ProvisioningServiceImpl implements ProvisioningService<Object, Object> {

    private static Log log = LogFactory.getLog(ProvisioningServiceImpl.class);

    private OauthAdminService authService = null;

    public ProvisioningServiceImpl() {
        authService = new OauthAdminServiceImpl();
    }

    @Override
    public Object provisionServiceProvider(Object t) {
        // TODO Auto-generated method stub

        log.info("=========================================================");
        log.info("provisionServiceProvider");

        log.info(authService.testMethod(""));
        log.info("=========================================================");
        //temp for testing
        //DiscoveryService ds = new DiscoveryServiceImpl();
        
        //ds.servceProviderCredentialDiscovery(discoveryServiceConfig, discoveryServiceDto);
        
        return null;
    }

}
