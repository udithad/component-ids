package com.wso2telco.sp.discovery;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;
import com.wso2telco.core.spprovisionservice.sp.entity.AdminServiceDto;
import com.wso2telco.core.spprovisionservice.sp.entity.DiscoveryServiceConfig;
import com.wso2telco.core.spprovisionservice.sp.entity.DiscoveryServiceDto;
import com.wso2telco.core.spprovisionservice.sp.entity.ProvisionType;
import com.wso2telco.core.spprovisionservice.sp.entity.ServiceProviderDto;
import com.wso2telco.sp.discovery.exception.DicoveryException;
import com.wso2telco.sp.entity.EksDiscovery;

public class RemoteExcKeySecretDiscovery extends RemoteDiscovery {

    private static Log log = LogFactory.getLog(RemoteExcKeySecretDiscovery.class);
    
    @Override
    public ServiceProviderDto servceProviderDiscovery(DiscoveryServiceConfig discoveryServiceConfig,
            DiscoveryServiceDto discoveryServiceDto) throws DicoveryException {
        log.info("Service Provider Exchange Key Discovery Call");
        String encodedBasicAuthCode = buildBasicAuthCode(discoveryServiceDto.getClientId(),
                discoveryServiceDto.getClientSecret());
        String requestMethod = HTTP_POST;
        String data = MSISDN + EQA_OPERATOR + discoveryServiceDto.getMsisdn();
        Map<String, String> requestProperties = buildRequestPropertiesEks(discoveryServiceConfig, discoveryServiceDto,
                encodedBasicAuthCode);

        EksDiscovery eksDiscovery = new Gson()
                .fromJson(getJsonWithDiscovery(buildEksEndPointUrl(discoveryServiceConfig), requestMethod, data,
                        requestProperties), EksDiscovery.class);
        return createServiceProviderDtoBy(eksDiscovery);
    }
    

    private String buildEksEndPointUrl(DiscoveryServiceConfig discoveryServiceConfig) {
        String endPointUrl = discoveryServiceConfig.getEksDiscoveryConfig().getServiceUrl() + QES_OPERATOR
                + REDIRECT_URL + EQA_OPERATOR + discoveryServiceConfig.getEksDiscoveryConfig().getRedirectUrl();
        return endPointUrl;
    }

    private Map<String, String> buildRequestPropertiesEks(DiscoveryServiceConfig discoveryServiceConfig,
            DiscoveryServiceDto discoveryServiceDto, String encodedBasicAuthCode) {
        Map<String, String> requestProperties = new HashMap<String, String>();
        requestProperties.put(CONTENT_TYPE_HEADER_KEY, CONTENT_TYPE_HEADER_VAL_TYPE_EKS);
        requestProperties.put(AUTHORIZATION_HEADER, BASIC + SPACE + encodedBasicAuthCode);
        return requestProperties;
    }
    

    private ServiceProviderDto createServiceProviderDtoBy(EksDiscovery eksDiscovery) {
        ServiceProviderDto serviceProviderDto = new ServiceProviderDto();
        if (eksDiscovery != null && eksDiscovery.getResponse() != null) {
            AdminServiceDto adminServiceDto = new AdminServiceDto();
            adminServiceDto.setOauthConsumerKey(eksDiscovery.getResponse().getClient_id());
            adminServiceDto.setOauthConsumerSecret(eksDiscovery.getResponse().getClient_secret());
            serviceProviderDto.setAdminServiceDto(adminServiceDto);
            serviceProviderDto.setExistance(ProvisionType.REMOTE);
        }
        return serviceProviderDto;
    }
    
}
