package com.opes.errTest.exception;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.hateos.JsonError;
import io.micronaut.http.hateos.Link;

import java.net.URI;

public abstract class HalParentException extends RuntimeException {
    private URI requestUri;
    private Boolean selfTemplate = false; //Default to false per RFC
    private String selfTitle;
    private URI selfDeprecationUri;
    private String selfHrefLang;
    private String selfName;
    private URI selfProfile;
    private MediaType selfType;
    private HttpStatus status;

    public HalParentException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public JsonError buildError() {
        JsonError je = new JsonError(getMessage());
        if (getRequestUri() != null) {
            Link.Builder builder = Link.build(getRequestUri());
            builder.templated(getSelfTemplate());
            if (getSelfTitle() != null) {
                builder.title(getSelfTitle());
            }
            if (getSelfDeprecationUri() != null) {
                builder.deprecation(getSelfDeprecationUri());
            }
            if (getSelfHrefLang() != null) {
                builder.hreflang(getSelfHrefLang());
            }
            if (getSelfName() != null) {
                builder.name(getSelfName());
            }
            if (getSelfProfile() != null) {
                builder.profile(getSelfProfile());
            }
            if (getSelfType() != null) {
                builder.type(getSelfType());
            }
            je.link(Link.SELF, builder.build());
        }
        return je;
    }

    public URI getRequestUri() {
        return requestUri;
    }

    public void setRequestUri(URI requestUri) {
        this.requestUri = requestUri;
    }

    public Boolean getSelfTemplate() {
        return selfTemplate;
    }

    public void setSelfTemplate(Boolean selfTemplate) {
        this.selfTemplate = selfTemplate;
    }

    public String getSelfTitle() {
        return selfTitle;
    }

    public void setSelfTitle(String selfTitle) {
        this.selfTitle = selfTitle;
    }

    public URI getSelfDeprecationUri() {
        return selfDeprecationUri;
    }

    public void setSelfDeprecationUri(URI selfDeprecationUri) {
        this.selfDeprecationUri = selfDeprecationUri;
    }

    public String getSelfHrefLang() {
        return selfHrefLang;
    }

    public void setSelfHrefLang(String selfHrefLang) {
        this.selfHrefLang = selfHrefLang;
    }

    public String getSelfName() {
        return selfName;
    }

    public void setSelfName(String selfName) {
        this.selfName = selfName;
    }

    public URI getSelfProfile() {
        return selfProfile;
    }

    public void setSelfProfile(URI selfProfile) {
        this.selfProfile = selfProfile;
    }

    public MediaType getSelfType() {
        return selfType;
    }

    public void setSelfType(MediaType selfType) {
        this.selfType = selfType;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }
}
