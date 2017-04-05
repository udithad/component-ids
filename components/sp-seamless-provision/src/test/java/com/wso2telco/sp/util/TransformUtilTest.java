package com.wso2telco.sp.util;

import org.junit.*;

import com.wso2telco.core.spprovisionservice.sp.entity.CrValidateDiscoveryConfig;
import com.wso2telco.core.spprovisionservice.sp.entity.DiscoveryServiceConfig;
import com.wso2telco.core.spprovisionservice.sp.entity.DiscoveryServiceDto;
import com.wso2telco.core.spprovisionservice.sp.entity.EksDisConfig;
import com.wso2telco.core.config.model.MobileConnectConfig;
import com.wso2telco.core.config.model.MobileConnectConfig.DiscoveryConfig;
import com.wso2telco.core.config.model.MobileConnectConfig.EksDiscoveryConfig;

public class TransformUtilTest {

    private static final String URL = "https://localhost:9443/playground2/oauth2.jsp";
    private static final String CLIENT_ID = "56233566988556222365223";
    private static final String SECTOR = "localhost";
    private static final String MSISDN = "0094719531809";
    private static final String EMPTY = "";

    /*
     * @BeforeClass
     * public static void initialize() {
     * ignore = 1;
     * }
     */

    @Test
    public void testTransofrmDiscoveryDto() {
        DiscoveryServiceDto discoveryDto = TransformUtil.transofrmDiscoveryDto(CLIENT_ID, URL);

        Assert.assertEquals(discoveryDto.getClientId(), CLIENT_ID);
        Assert.assertEquals(discoveryDto.getSectorId(), SECTOR);
    }

    @Test
    public void testTransofrmDiscoveryDtoEmptyURL() {
        DiscoveryServiceDto discoveryDto = TransformUtil.transofrmDiscoveryDto(CLIENT_ID, EMPTY);

        Assert.assertEquals(discoveryDto.getClientId(), CLIENT_ID);
        Assert.assertEquals(discoveryDto.getSectorId(), null);
    }

    @Test
    public void testTransformEksDiscoveryConfig() {
        EksDiscoveryConfig discoveryConf = new EksDiscoveryConfig();
        discoveryConf.setMsisdn(MSISDN);
        discoveryConf.setRedirectUrl(URL);
        discoveryConf.setServiceUrl(URL);
        EksDisConfig discoveryDto = TransformUtil.transformEksDiscoveryConfig(discoveryConf);
        Assert.assertEquals(discoveryDto.getMsisdn(), MSISDN);
        Assert.assertEquals(discoveryDto.getRedirectUrl(), URL);
        Assert.assertEquals(discoveryDto.getServiceUrl(), URL);
    }

    @Test
    public void testTransoformCrValidateDiscoveryConfig() {
        CrValidateDiscoveryConfig v = new CrValidateDiscoveryConfig();
        com.wso2telco.core.config.model.MobileConnectConfig.CrValidateDiscoveryConfig conf = new com.wso2telco.core.config.model.MobileConnectConfig.CrValidateDiscoveryConfig();
        conf.setServiceUrl(URL);
        v = TransformUtil.transoformCrValidateDiscoveryConfig(conf);
        Assert.assertEquals(v.getServiceUrl(), URL);
    }

    @Test
    public void testTransformDiscoveryConfig() {
        MobileConnectConfig mobileConnectConfig = new MobileConnectConfig();
        DiscoveryServiceConfig discoveryServiceConfig = new DiscoveryServiceConfig();
        DiscoveryConfig discoveryConfig = new DiscoveryConfig();
        TransformUtil.transformDiscoveryConfig(discoveryConfig, mobileConnectConfig);
        Assert.assertEquals(discoveryServiceConfig.getCrValidateDiscoveryConfig(), null);
        Assert.assertEquals(discoveryServiceConfig.getEksDiscoveryConfig(), null);
    }
    
    @Test
    public void testValidationUtil(){
        ValidationUtil validationUtil = new ValidationUtil();
    }
    /*
     * @After
     * public void collector() {
     * ignore = 0;
     * }
     */
}
