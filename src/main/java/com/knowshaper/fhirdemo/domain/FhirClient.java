/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.knowshaper.fhirdemo.domain;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BearerTokenAuthInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;
import org.hl7.fhir.r4.model.CapabilityStatement;
import org.hl7.fhir.r4.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kris
 */
public class FhirClient {

    Logger logger = LoggerFactory.getLogger(FhirClient.class);

    private final UUID id;
    private final FhirContext fhirContext;
    private final String fhirServerAddress;
    private final String launchToken;
    private final String appClientId;
    private final String appClientSecret;
    private final String redirect_uri;
    private AccessGrantedData accessData = null;

    private CapabilityStatementWrapper capabilityStatement;

    public FhirClient(UUID id, FhirContext ctx, String fhirServerAddress, String launchToken, String appClientId, String appClientSecret, String redirectUri) throws IllegalStateException {
        this.fhirContext = ctx;
        this.fhirServerAddress = fhirServerAddress;
        this.launchToken = launchToken;
        this.appClientId = appClientId;
        this.appClientSecret = appClientSecret;
        this.redirect_uri = redirectUri;
        this.id = id;

        this.capabilityStatement = this.fetchCapabilityStatement();
        if (null == this.capabilityStatement) {
            throw new IllegalStateException("unable to retrieve capability statement");
        }
    }

    public boolean hasAccess() {
        return this.accessData != null;
    }

    private CapabilityStatementWrapper fetchCapabilityStatement() {
        IGenericClient hapiClient = this.fhirContext.newRestfulGenericClient(fhirServerAddress);
        CapabilityStatement hapiCapsStatement = hapiClient.capabilities().ofType(CapabilityStatement.class).execute();
        if (null != hapiCapsStatement) {
            return this.capabilityStatement = new CapabilityStatementWrapper(hapiCapsStatement);
        } else {
            return null;
        }
    }

    public void requestAuthorizationCode() throws MalformedURLException, IOException, IllegalStateException {
        String request_body = "response_type=code&client_id=" + appClientId + "&redirect_uri=" + redirect_uri + "&scope=launch" + "&launch=" + launchToken + "&aud=" + fhirServerAddress + "&state=" + id;
        logger.info("request_body: " + request_body);

        String authorizeEndpointUrl = this.capabilityStatement.getOauthAuthorizationEndpointUrlFirstRep();

        if (authorizeEndpointUrl == null) {
            throw new IllegalStateException("Unable to make a request to the authorization endpoint because capability statement does not contain required information");
        }
        URL url = new URL(authorizeEndpointUrl);

        String response = this.post(url, request_body);

        logger.info("requestAuthorizationCode response string: " + response);
    }

    public void requestAccess(String authorizationCode) throws UnsupportedEncodingException, MalformedURLException, IOException, IllegalStateException {

        String authorization = this.appClientId + ":" + appClientSecret;
        String authorizationEncoded = Base64.getEncoder().encodeToString(authorization.getBytes(StandardCharsets.UTF_8));

        String requestBody = "grant_type=authorization_code&code=" + authorizationCode + "&redirect_uri=" + redirect_uri;
        logger.info("requestBody: " + requestBody);

        String tokenEndpointUrl = this.capabilityStatement.getOauthTokenEndpointUrlFirstRep();

        if (tokenEndpointUrl == null) {
            throw new IllegalStateException("Unable to make a request to the token endpoint because capability statement does not contain required information");
        }

        URL url = new URL(tokenEndpointUrl);

        String response = this.post(url, requestBody, authorizationEncoded);

        ObjectMapper objectMapper = new ObjectMapper();
        this.accessData = objectMapper.readValue(response, AccessGrantedData.class);
    }

    private String post(URL url, String request_body) throws IOException {
        return this.post(url, request_body, null);
    }

    private String post(URL url, String request_body, String authorization) throws IOException {
        byte[] postData = request_body.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        if (null != authorization) {
            con.setRequestProperty("Authorization", "Basic " + authorization);
        }
        con.setDoOutput(true);
        con.setRequestProperty("Content-Length", Integer.toString(postDataLength));

        try ( DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
            wr.write(postData);
        }

        StringBuilder response;
        try ( BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }
        con.disconnect();

        return response.toString();
    }

    public Patient getPatient() throws IllegalStateException {

        if (null == this.accessData) {
            throw new IllegalStateException("don't have access to fhir server");
        }

        BearerTokenAuthInterceptor authInterceptor = new BearerTokenAuthInterceptor(this.accessData.getAccess_token());
        IGenericClient genericClient = this.fhirContext.newRestfulGenericClient(this.capabilityStatement.getImplementationComponentUrl());
        genericClient.registerInterceptor(authInterceptor);
        Patient p = genericClient.read().resource(Patient.class).withId(this.accessData.getPatient()).execute();
        return p;
    }

    public AccessGrantedData accessData() {
        return this.accessData;
    }
}
