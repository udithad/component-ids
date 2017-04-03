package com.wso2telco.sp.discovery.service;

import com.wso2telco.core.spprovisionservice.sp.entity.DiscoveryServiceConfig;
import com.wso2telco.core.spprovisionservice.sp.entity.DiscoveryServiceDto;
import com.wso2telco.core.spprovisionservice.sp.entity.ServiceProviderDto;
import com.wso2telco.sp.discovery.exception.DicoveryException;

public interface DiscoveryService {

    public ServiceProviderDto servceProviderCredentialDiscovery(DiscoveryServiceConfig discoveryServiceConfig,
            DiscoveryServiceDto discoveryServiceDto);

    public ServiceProviderDto servceProviderEksDiscovery(DiscoveryServiceConfig discoveryServiceConfig,
            DiscoveryServiceDto discoveryServiceDto);

}
