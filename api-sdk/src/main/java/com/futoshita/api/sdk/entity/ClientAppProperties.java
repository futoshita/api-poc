package com.futoshita.api.sdk.entity;

public class ClientAppProperties {

    private String apiBaseUrl;
    private String accessTokenKey;
    private String accessTokenSecret;
    private String consumerKey;
    private String consumerSecret;
    private String locale;
    private String oauth1AccessTokenUri;
    private String oauth1AuthorizationUri;
    private String oauth1RequestTokenUri;

    public String getApiBaseUrl() {
        return apiBaseUrl;
    }

    public void setApiBaseUrl(String apiBaseUrl) {
        this.apiBaseUrl = apiBaseUrl;
    }

    public String getAccessTokenKey() {
        return accessTokenKey;
    }

    public void setAccessTokenKey(String accessTokenKey) {
        this.accessTokenKey = accessTokenKey;
    }

    public String getAccessTokenSecret() {
        return accessTokenSecret;
    }

    public void setAccessTokenSecret(String accessTokenSecret) {
        this.accessTokenSecret = accessTokenSecret;
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }

    public String getConsumerSecret() {
        return consumerSecret;
    }

    public void setConsumerSecret(String consumerSecret) {
        this.consumerSecret = consumerSecret;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getOauth1AccessTokenUri() {
        return oauth1AccessTokenUri;
    }

    public void setOauth1AccessTokenUri(String oauth1AccessTokenUri) {
        this.oauth1AccessTokenUri = oauth1AccessTokenUri;
    }

    public String getOauth1AuthorizationUri() {
        return oauth1AuthorizationUri;
    }

    public void setOauth1AuthorizationUri(String oauth1AuthorizationUri) {
        this.oauth1AuthorizationUri = oauth1AuthorizationUri;
    }

    public String getOauth1RequestTokenUri() {
        return oauth1RequestTokenUri;
    }

    public void setOauth1RequestTokenUri(String oauth1RequestTokenUri) {
        this.oauth1RequestTokenUri = oauth1RequestTokenUri;
    }
}
