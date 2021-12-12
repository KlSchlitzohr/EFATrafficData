package de.klschlitzohr.efatrafficdata.scraper.utils;


import de.klschlitzohr.efatrafficdata.api.configuration.VerkehrsDatenConfiguration;
import de.klschlitzohr.efatrafficdata.scraper.Main;
import de.klschlitzohr.efatrafficdata.scraper.exceptions.AuthorizationFailedException;
import de.klschlitzohr.efatrafficdata.scraper.exceptions.RateLimitingExecption;
import lombok.extern.log4j.Log4j2;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Log4j2
public class HttpsRequestBuilder {

    private final String url;
    private final HttpsRequestType httpsRequestType;
    private String payload;
    private HashMap<String, String> queryParameters;
    private HashMap<String, String> headerParameters;
    private final VerkehrsDatenConfiguration verkehrsDatenConfiguration;

    public HttpsRequestBuilder(String url, HttpsRequestType httpsRequestType) {
        this.url = url;
        this.httpsRequestType = httpsRequestType;
        verkehrsDatenConfiguration = Main.getVerkehrsDaten().getConfiguration();
    }

    public HttpResponse getResponse() {
        long duration = System.currentTimeMillis();
        try {
            String urlString = this.url;
            if (queryParameters != null && !queryParameters.isEmpty())
                urlString += "?" + getParamsString(queryParameters);
            URL url = new URL(urlString);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", "Bearer " + verkehrsDatenConfiguration.getEfaAPIKey());
            if (headerParameters != null && !headerParameters.isEmpty())
                headerParameters.keySet().forEach(headerParameter ->
                        connection.setRequestProperty(headerParameter,headerParameters.get(headerParameter)));
            connection.setDoOutput(true);
            connection.setRequestMethod(this.httpsRequestType.getRequestType());
            if (payload != null) {
                OutputStream os = connection.getOutputStream();
                os.write(payload.getBytes());
                os.flush();
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringWriter out = new StringWriter(connection.getContentLength() > 0 ? connection.getContentLength() : 2048);
            while ((line = reader.readLine()) != null) {
                out.append(line);
            }
            int rc = manageResponseCode(connection);
            return new HttpResponse(out.toString(),System.currentTimeMillis() - duration,rc);
        } catch (IOException e) {
            log.error(e);
        }
        return null;
    }

    private int manageResponseCode(HttpsURLConnection httpsURLConnection) {
        try {
            int responseCode = httpsURLConnection.getResponseCode();
            if (responseCode == 401)
                throw new AuthorizationFailedException();
            else if (responseCode == 429)
                throw new RateLimitingExecption();
            return responseCode;
        } catch (IOException | AuthorizationFailedException | RateLimitingExecption e) {
            log.error(e);
        }
        return 0;
    }

    private String getParamsString(Map<String, String> params)
            throws UnsupportedEncodingException{
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            result.append("&");
        }

        String resultString = result.toString();
        return resultString.length() > 0
                ? resultString.substring(0, resultString.length() - 1)
                : resultString;
    }

    public HttpsRequestBuilder setPostParams(HashMap<String, String> queryParameters) {
        this.queryParameters = queryParameters;
        return this;
    }

    public HttpsRequestBuilder setHeaderParameters(HashMap<String, String> headerParameters) {
        this.headerParameters = headerParameters;
        return this;
    }

    public HttpsRequestBuilder setPayload(String payload) {
        this.payload = payload;
        return this;
    }

}
