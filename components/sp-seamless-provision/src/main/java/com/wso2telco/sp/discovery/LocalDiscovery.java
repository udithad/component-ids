package com.wso2telco.sp.discovery;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wso2telco.core.pcrservice.PCRGeneratable;
import com.wso2telco.core.pcrservice.Returnable;
import com.wso2telco.core.pcrservice.exception.PCRException;
import com.wso2telco.core.spprovisionservice.sp.entity.DiscoveryServiceConfig;
import com.wso2telco.core.spprovisionservice.sp.entity.DiscoveryServiceDto;
import com.wso2telco.core.spprovisionservice.sp.entity.ProvisionType;
import com.wso2telco.core.spprovisionservice.sp.entity.ServiceProviderDto;
import com.wso2telco.sp.discovery.exception.DicoveryException;
import com.wso2telco.sp.internal.SpProvisionPcrDataHolder;
import com.wso2telco.sp.util.ValidationUtil;

public class LocalDiscovery extends DiscoveryLocator {

    private static Log log = LogFactory.getLog(LocalDiscovery.class);

    @Override
    public ServiceProviderDto servceProviderDiscovery(DiscoveryServiceConfig discoveryServiceConfig,
            DiscoveryServiceDto discoveryServiceDto) throws DicoveryException {

        log.info("Performing Local Discovery on EKS");
        ServiceProviderDto serviceProviderDto = null;
        boolean isAppAvailable = false;

        ValidationUtil.validateInuts(discoveryServiceDto.getSectorId(), discoveryServiceDto.getClientId());
        populateMsisdnFrom(discoveryServiceConfig, discoveryServiceDto);
        isAppAvailable = checkLocally(discoveryServiceConfig.isPcrServiceEnabled(), discoveryServiceDto.getSectorId(),
                discoveryServiceDto.getClientId(), discoveryServiceDto.getClientSecret());
        if (!isAppAvailable) {
            serviceProviderDto = checkRemotlyDiscovery(discoveryServiceConfig, discoveryServiceDto);
        } else {
            serviceProviderDto = constructDefaultSp();
        }

        return serviceProviderDto;
    }

    private ServiceProviderDto constructDefaultSp() {
        ServiceProviderDto serviceProviderDto = null;
        serviceProviderDto = new ServiceProviderDto();
        serviceProviderDto.setExistance(ProvisionType.LOCAL);
        return serviceProviderDto;
    }

    private ServiceProviderDto checkRemotlyDiscovery(DiscoveryServiceConfig discoveryServiceConfig,
            DiscoveryServiceDto discoveryServiceDto) throws DicoveryException {
        ServiceProviderDto serviceProviderDto = null;
        if (getNextDiscovery() != null) {
            serviceProviderDto = getNextDiscovery().servceProviderDiscovery(discoveryServiceConfig,
                    discoveryServiceDto);
        }

        return serviceProviderDto;
    }

    private boolean checkLocally(boolean isPcrServiceEnabled, String sectorId, String clientId, String clientSecret)
            throws DicoveryException {
        boolean isAppAvailable = false;
        if (isPcrServiceEnabled) {
            isAppAvailable = checkSpAvailabilityInmemory(sectorId, clientId);
        } else {
            // Call SP-PrOVISIONING SERVICE GET BY CLIent id method
            if (clientSecret != null && !clientSecret.isEmpty()) {
                // check secret ==
                // then return true
                // else false
            }
        }
        return isAppAvailable;
    }

    private void populateMsisdnFrom(DiscoveryServiceConfig discoveryServiceConfig,
            DiscoveryServiceDto discoveryServiceDto) {
        if (discoveryServiceConfig != null && discoveryServiceConfig.getEksDiscoveryConfig() != null
                && discoveryServiceConfig.getEksDiscoveryConfig().getMsisdn() != null
                && !discoveryServiceConfig.getEksDiscoveryConfig().getMsisdn().isEmpty()) {
            discoveryServiceDto.setMsisdn(discoveryServiceConfig.getEksDiscoveryConfig().getMsisdn());
        }
    }

    private boolean checkSpAvailabilityInmemory(String sector, String clientId) throws DicoveryException {
        boolean isAvailable = false;
        log.info("Performing Sp availability in redis.");
        Returnable returnable = null;
        try {
            PCRGeneratable pcrGeneratable = SpProvisionPcrDataHolder.getInstance().getPcrGeneratable();
            returnable = pcrGeneratable.isAppAvailableFor(sector, clientId);
        } catch (PCRException e) {
            throw new DicoveryException("Error Occured Whicle Local Discovery Operation" + e.getMessage(), true);
        }
        isAvailable = returnable.getAvailablity();
        return isAvailable;

    }

}
