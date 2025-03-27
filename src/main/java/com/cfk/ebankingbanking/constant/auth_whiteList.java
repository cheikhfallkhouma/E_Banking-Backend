package com.cfk.ebankingbanking.constant;

import org.springframework.http.HttpMethod;

public abstract class auth_whiteList {
 //   public static final HttpMethod AUTH_WHITELIST = ;
    private static final String[] AUTH_WHITELIST = {
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            "/v3/api-docs/**",
            "/swagger-ui/**"
    };
}
