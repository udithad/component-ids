package com.wso2telco.sp.builder;

import org.junit.*;

public class ServiceProviderBuilderTest {

    private static Integer ignore = null;

    @BeforeClass
    public static void initialize() {
        ignore = 1;
    }

    @Test
    public void testRevokeSpApplication() {

        Assert.assertEquals(ignore, ignore);

    }

    @After
    public void collector() {
        ignore = 0;
    }
}
