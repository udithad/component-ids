package com.wso2telco.sp.discovery;

public abstract class DiscoveryLocator<K, T> {

    private DiscoveryLocator<K, T> nextDiscovery;

    public abstract K findSpBy(String clientId);

    private K nextDiscoveryOperation(String clientId) {
        return this.nextDiscovery.findSpBy(clientId);
    }

    public DiscoveryLocator<K, T> getNextDiscovery() {
        return nextDiscovery;
    }

    public void setNextDiscovery(DiscoveryLocator<K, T> nextDiscovery) {
        this.nextDiscovery = nextDiscovery;
    }

}
