package com.wso2telco.sp.discovery;

import org.junit.*;


public class DiscoveryLocatorTest {

    /*
     * @BeforeClass
     * public static void initialize() {
     * ignore = 1;
     * }
     */

    @Test
    public void testLocalDiscoveryLocators() {
        // Apis ps = new Apis();
        DiscoveryLocator discoveryLocator = new LocalDiscovery();
        RemoteCredentialDiscovery remoteCredentialDiscovery = new RemoteCredentialDiscovery();
        discoveryLocator.setNextDiscovery(remoteCredentialDiscovery);
        
        
        Assert.assertEquals(discoveryLocator.getNextDiscovery(), remoteCredentialDiscovery);

    }

   
    /*
     * @After
     * public void collector() {
     * ignore = 0;
     * }
     */

}
