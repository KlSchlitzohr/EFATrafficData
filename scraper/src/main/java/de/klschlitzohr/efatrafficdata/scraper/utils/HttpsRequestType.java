package de.klschlitzohr.efatrafficdata.scraper.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum HttpsRequestType {

    GET("GET"), POST("POST");

    @Getter
    private final String requestType;

}
