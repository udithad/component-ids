package com.wso2telco.sp.discovery;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;
import com.wso2telco.core.spprovisionservice.sp.entity.AdminServiceDto;
import com.wso2telco.core.spprovisionservice.sp.entity.DiscoveryServiceConfig;
import com.wso2telco.core.spprovisionservice.sp.entity.DiscoveryServiceDto;
import com.wso2telco.core.spprovisionservice.sp.entity.ServiceProviderDto;
import com.wso2telco.sp.discovery.exception.DicoveryException;
import com.wso2telco.sp.entity.CrValidateRes;

public class RemoteCredentialDiscovery extends RemoteDiscovery{

    private static Log log = LogFactory.getLog(RemoteCredentialDiscovery.class);
    
    @Override
    public ServiceProviderDto servceProviderDiscovery(DiscoveryServiceConfig discoveryServiceConfig,
            DiscoveryServiceDto discoveryServiceDto) throws DicoveryException {
        log.info("Service Provider Credentail Discovery Call");
        String encodedBasicAuthCode = buildBasicAuthCode(discoveryServiceDto.getClientId(),
                discoveryServiceDto.getClientSecret());
        String requestMethod = HTTP_POST;
        Map<String, String> requestProperties = buildRequestPropertiesCr(discoveryServiceConfig, discoveryServiceDto,
                encodedBasicAuthCode);

        CrValidateRes crValidateRes = new Gson()
                .fromJson(getJsonWithDiscovery(buildCrEndPointUrl(discoveryServiceConfig, discoveryServiceDto),
                        requestMethod, null, requestProperties), CrValidateRes.class);
        return createServiceProviderDtoBy(crValidateRes);
    }
    
    private Map<String, String> buildRequestPropertiesCr(DiscoveryServiceConfig discoveryServiceConfig,
            DiscoveryServiceDto discoveryServiceDto, String encodedBasicAuthCode) {
        Map<String, String> requestProperties = new HashMap<String, String>();
        requestProperties.put(ACCEPT, CONTENT_TYPE_HEADER_VAL_TYPE_CR);
        requestProperties.put(AUTHORIZATION_HEADER, BASIC + SPACE + encodedBasicAuthCode);
        return requestProperties;
    }
    
    private String buildCrEndPointUrl(DiscoveryServiceConfig discoveryServiceConfig,
            DiscoveryServiceDto discoveryServiceDto) {
        String endPointUrl = discoveryServiceConfig.getCrValidateDiscoveryConfig().getServiceUrl() + QES_OPERATOR
                + CLIENT_ID + EQA_OPERATOR + discoveryServiceDto.getClientId() + AMP_OPERATOR + CLIENT_SECRET
                + EQA_OPERATOR + discoveryServiceDto.getClientSecret();
        return endPointUrl;
    }
    
    private ServiceProviderDto createServiceProviderDtoBy(CrValidateRes crValidateRes) {
        ServiceProviderDto serviceProviderDto = new ServiceProviderDto();
        if (crValidateRes != null && crValidateRes.getApplication() != null) {
            serviceProviderDto.setApplicationName(crValidateRes.getApplication().getAppName());
            AdminServiceDto adminServiceDto = new AdminServiceDto();
            adminServiceDto.setCallbackUrl(crValidateRes.getApplication().getRedirectUri());
            serviceProviderDto.setAdminServiceDto(adminServiceDto);
        }
        return serviceProviderDto;
    }
}
