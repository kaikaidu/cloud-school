package com.amway.acti.base.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Properties;

@Component
@ConfigurationProperties(prefix = "azure.config")
public class StorageConfig {
    private Properties props = new Properties();
    private String accountName;

    private String accountKey;

    private String defaultEndpointsProtocol;

    private Map<String, String> containers;

    public StorageConfig() {
    }

    public Properties getProps() {
        return props;
    }

    public void setProps(Properties props) {
        this.props = props;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountKey() {
        return accountKey;
    }

    public void setAccountKey(String accountKey) {
        this.accountKey = accountKey;
    }

    public String getDefaultEndpointsProtocol() {
        return defaultEndpointsProtocol;
    }

    public void setDefaultEndpointsProtocol(String defaultEndpointsProtocol) {
        this.defaultEndpointsProtocol = defaultEndpointsProtocol;
    }

    public Map<String, String> getContainers() {
        return containers;
    }

    public void setContainers(Map<String, String> containers) {
        this.containers = containers;
    }
}