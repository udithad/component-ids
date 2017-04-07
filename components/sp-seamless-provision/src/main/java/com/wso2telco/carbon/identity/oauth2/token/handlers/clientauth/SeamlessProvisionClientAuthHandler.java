package com.wso2telco.carbon.identity.oauth2.token.handlers.clientauth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.oauth.IdentityOAuthAdminException;
import org.wso2.carbon.identity.oauth.common.exception.InvalidOAuthClientException;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.oauth2.dto.OAuth2AccessTokenReqDTO;
import org.wso2.carbon.identity.oauth2.token.OAuthTokenReqMessageContext;
import org.wso2.carbon.identity.oauth2.token.handlers.clientauth.AbstractClientAuthHandler;
import org.wso2.carbon.identity.oauth2.util.OAuth2Util;

import com.wso2telco.core.config.model.MobileConnectConfig;
import com.wso2telco.core.config.model.MobileConnectConfig.Config;
import com.wso2telco.core.config.model.MobileConnectConfig.DiscoveryConfig;
import com.wso2telco.core.config.service.ConfigurationService;
import com.wso2telco.core.config.service.ConfigurationServiceImpl;
import com.wso2telco.core.pcrservice.util.SectorUtil;
import com.wso2telco.core.spprovisionservice.sp.entity.AdminServiceDto;
import com.wso2telco.core.spprovisionservice.sp.entity.DiscoveryServiceConfig;
import com.wso2telco.core.spprovisionservice.sp.entity.DiscoveryServiceDto;
import com.wso2telco.core.spprovisionservice.sp.entity.ProvisionType;
import com.wso2telco.core.spprovisionservice.sp.entity.ServiceProviderDto;
import com.wso2telco.core.spprovisionservice.sp.entity.SpProvisionDto;
import com.wso2telco.core.spprovisionservice.sp.exception.SpProvisionServiceException;
import com.wso2telco.sp.discovery.service.DiscoveryService;
import com.wso2telco.sp.discovery.service.impl.DiscoveryServiceImpl;
import com.wso2telco.sp.provision.service.ProvisioningService;
import com.wso2telco.sp.provision.service.impl.ProvisioningServiceImpl;
import com.wso2telco.sp.util.TransformUtil;

public class SeamlessProvisionClientAuthHandler extends AbstractClientAuthHandler {

    private static Log log = LogFactory.getLog(SeamlessProvisionClientAuthHandler.class);
    private DiscoveryService discoveryService;
    private static MobileConnectConfig mobileConnectConfigs = null;
    private static ConfigurationService configurationService = new ConfigurationServiceImpl();
    private ProvisioningService provisioningService = null;

    public SeamlessProvisionClientAuthHandler() {
        discoveryService = new DiscoveryServiceImpl();
        mobileConnectConfigs = configurationService.getDataHolder().getMobileConnectConfig();
    }

    @Override
    public boolean authenticateClient(OAuthTokenReqMessageContext tokReqMsgCtx) throws IdentityOAuth2Exception {

        seamlessProvisioning(tokReqMsgCtx);
        boolean isAuthenticated = super.authenticateClient(tokReqMsgCtx);

        if (!isAuthenticated) {
            OAuth2AccessTokenReqDTO oAuth2AccessTokenReqDTO = tokReqMsgCtx.getOauth2AccessTokenReqDTO();
            try {
                return OAuth2Util.authenticateClient(oAuth2AccessTokenReqDTO.getClientId(),
                        oAuth2AccessTokenReqDTO.getClientSecret());
            } catch (IdentityOAuthAdminException e) {
                throw new IdentityOAuth2Exception("Error while authenticating client", e);
            } catch (InvalidOAuthClientException e) {
                throw new IdentityOAuth2Exception("Invalid Client : " + oAuth2AccessTokenReqDTO.getClientId(), e);
            }
        } else {
            return true;
        }
    }

    private void seamlessProvisioning(OAuthTokenReqMessageContext tokReqMsgCtx) throws IdentityOAuth2Exception {
        log.info("Initiating seamless provisioning procees");
        if (mobileConnectConfigs.isSeamlessProvisioningEnabled()) {
            ServiceProviderDto serviceProviderDto = discoverServiceProvider(tokReqMsgCtx.getOauth2AccessTokenReqDTO());
            if (serviceProviderDto != null && !serviceProviderDto.getExistance().equals(ProvisionType.LOCAL)
                    && serviceProviderDto.getAdminServiceDto() != null) {
                log.info("Service Provider does not contain same credentials. Provisioning new credentials...");
                serviceProviderSeamlessProvision(serviceProviderDto);
            } else {
                log.info("Service Provider does not found LOCALLY OR REMOTELY... Auth token creation failed...");
                throw new IdentityOAuth2Exception("Service Provider Not Found");
            }
        }
    }

    private void serviceProviderSeamlessProvision(ServiceProviderDto serviceProvider) {

        SpProvisionDto spProvisionDto = null;

        try {

            boolean isSeamlessProvisioningEnabled = mobileConnectConfigs.isSeamlessProvisioningEnabled();
            MobileConnectConfig.Config config = mobileConnectConfigs.getSpProvisionConfig().getConfig();

            if (isSeamlessProvisioningEnabled) {
                if (config != null) {
                    spProvisionDto = getServiceProviderDto(serviceProvider, config);
                    provisioningService = new ProvisioningServiceImpl();
                    provisioningService.provisionServiceProvider(spProvisionDto);
                } else {
                    log.error("Config null");
                }
            }
        } catch (SpProvisionServiceException e) {
            log.error("Error occurred in provisioning a Service Provider " + e.getMessage());
        }
    }

    private SpProvisionDto getServiceProviderDto(ServiceProviderDto serviceProvider,
            MobileConnectConfig.Config config) {
        SpProvisionDto spProvisionDto = new SpProvisionDto();

        String applicationName = serviceProvider.getApplicationName();
        String description = serviceProvider.getDescription();
        String cutomerKey = serviceProvider.getAdminServiceDto().getOauthConsumerKey().replaceAll("x-", "");
        String secretKey = serviceProvider.getAdminServiceDto().getOauthConsumerSecret().replaceAll("x-", "");

        ServiceProviderDto serviceProviderDto = new ServiceProviderDto();
        serviceProviderDto.setApplicationName(applicationName);
        serviceProviderDto.setDescription(description);
        serviceProviderDto.setInboundAuthKey(cutomerKey);
        serviceProviderDto.setPropertyValue(secretKey);
        serviceProviderDto.setAlwaysSendMappedLocalSubjectId(config.isAlwaysSendMappedLocalSubjectId());
        serviceProviderDto.setLocalClaimDialect(config.isLocalClaimDialect());
        serviceProviderDto.setInboundAuthType(config.getInboundAuthType());
        serviceProviderDto.setConfidential(config.isConfidential());
        serviceProviderDto.setDefaultValue(config.getDefaultValue());
        serviceProviderDto.setPropertyName(config.getPropertyName());
        serviceProviderDto.setPropertyRequired(config.isPropertyRequired());
        serviceProviderDto.setProvisioningEnabled(config.isProvisioningEnabled());
        serviceProviderDto.setProvisioningUserStore(config.getProvisioningUserStore());
        String idpRoles[] = { applicationName };
        serviceProviderDto.setIdpRoles(idpRoles);
        serviceProviderDto.setSaasApp(config.isSaasApp());
        serviceProviderDto.setLocalAuthenticatorConfigsDisplayName(config.getLocalAuthenticatorConfigsDisplayName());
        serviceProviderDto.setLocalAuthenticatorConfigsEnabled(config.isLocalAuthenticatorConfigsEnabled());
        serviceProviderDto.setLocalAuthenticatorConfigsName(config.getLocalAuthenticatorConfigsName());
        serviceProviderDto.setLocalAuthenticatorConfigsValid(config.isLocalAuthenticatorConfigsValid());
        serviceProviderDto.setLocalAuthenticatorConfigsAuthenticationType(
                config.getLocalAuthenticatorConfigsAuthenticationType());

        // Set values for spProvisionConfig

        serviceProviderDto.setAdminServiceDto(getAdminServiceDto(serviceProvider, config));
        serviceProviderDto.setExistance(ProvisionType.LOCAL);

        // Set Values for SpProvisionDTO
        spProvisionDto.setServiceProviderDto(serviceProviderDto);
        spProvisionDto.setProvisionType(ProvisionType.LOCAL);
        spProvisionDto.setDiscoveryServiceDto(null);
        return spProvisionDto;

    }

    private AdminServiceDto getAdminServiceDto(ServiceProviderDto serviceProvider, MobileConnectConfig.Config config) {

        String applicationName = serviceProvider.getApplicationName();
        String cutomerKey = serviceProvider.getAdminServiceDto().getOauthConsumerKey();
        String secretKey = serviceProvider.getAdminServiceDto().getOauthConsumerSecret();;
        String callbackUrl = serviceProvider.getAdminServiceDto().getCallbackUrl();

        AdminServiceDto adminServiceDto = new AdminServiceDto();
        adminServiceDto.setApplicationName(applicationName);
        adminServiceDto.setCallbackUrl(callbackUrl);
        adminServiceDto.setOauthVersion(config.getoAuthVersion());
        adminServiceDto.setGrantTypes(config.getGrantTypes());
        adminServiceDto.setOauthConsumerKey(cutomerKey);
        adminServiceDto.setOauthConsumerSecret(secretKey);
        adminServiceDto.setPkceMandatory(config.isPkceMandatory());
        adminServiceDto.setPkceSupportPlain(config.isPkceSupportPlain());
        return adminServiceDto;

    }

    private ServiceProviderDto discoverServiceProvider(OAuth2AccessTokenReqDTO oAuth2AccessTokenReqDTO)
            throws IdentityOAuth2Exception {
        ServiceProviderDto serviceProviderDto = null;
        try {
            serviceProviderDto = discoveryService.servceProviderEksDiscovery(readDiscoveryConfigs(),
                    getDiscoveryServiceDto(oAuth2AccessTokenReqDTO));
        } catch (Exception e) {
            log.error("" + e.getMessage());
        }
        return serviceProviderDto;
    }

    private DiscoveryServiceConfig readDiscoveryConfigs() {
        return TransformUtil.transformDiscoveryConfig(getDiscoveryConfig(), getMobileConnectConfig());
    }

    private DiscoveryServiceDto getDiscoveryServiceDto(OAuth2AccessTokenReqDTO oAuth2AccessTokenReqDTO) {
        DiscoveryServiceDto discoveryServiceDto = new DiscoveryServiceDto();
        discoveryServiceDto.setClientId(oAuth2AccessTokenReqDTO.getClientId());
        discoveryServiceDto.setClientSecret(oAuth2AccessTokenReqDTO.getClientSecret());
        String sectorId = SectorUtil.getSectorIdFromUrl(oAuth2AccessTokenReqDTO.getCallbackURI());
        discoveryServiceDto.setSectorId(sectorId);
        return discoveryServiceDto;
    }

    private DiscoveryConfig getDiscoveryConfig() {
        mobileConnectConfigs = configurationService.getDataHolder().getMobileConnectConfig();
        return mobileConnectConfigs.getDiscoveryConfig();
    }

    private MobileConnectConfig getMobileConnectConfig() {
        mobileConnectConfigs = configurationService.getDataHolder().getMobileConnectConfig();
        return mobileConnectConfigs;
    }

}
