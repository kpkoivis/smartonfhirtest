/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.knowshaper.fhirdemo.service;

import ca.uhn.fhir.context.FhirContext;
import com.knowshaper.fhirdemo.domain.FhirClient;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import org.hl7.fhir.r4.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 *
 * @author kris
 */
@Service
public class FhirTestServiceImpl implements FhirTestService {

    Logger logger = LoggerFactory.getLogger(FhirTestServiceImpl.class);

    @Value("${epic_client_id}")
    private String appClientId;

    @Value("${epic_client_secret}")
    private String appClientSecret;

    @Value("${redirect_uri}")
    private String redirect_uri;

    private FhirContext ctx;
    
    private final HashMap<UUID, FhirClient> clients = new HashMap();
    private final HashMap<UUID, CompletableFuture<String>> redirectResults = new HashMap();

    @PostConstruct
    public void setup() {
        this.ctx = FhirContext.forR4();
    }

    @Override
    public String handleLaunchQuery(String fhirServerAddress, String launchToken) {

        UUID instanceId = UUID.randomUUID();

        CompletableFuture<String> redirectResult = new CompletableFuture<>();
        this.redirectResults.put(instanceId, redirectResult);

        try {
            FhirClient client = new FhirClient(instanceId, this.ctx, fhirServerAddress, launchToken, this.appClientId, this.appClientSecret, this.redirect_uri);

            this.clients.put(instanceId, client);

            try {
                client.requestAuthorizationCode();
            } catch (IOException ex) {
                logger.info("requestAuthorizationCode exception: " + ex);
                return ex.getMessage();
            }

            try {
                return redirectResult.get();
            } catch (InterruptedException | ExecutionException ex) {
                java.util.logging.Logger.getLogger(FhirTestServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                return ex.toString();
            }

        } catch (IllegalStateException ex) {
            String s = "fhir client ininitation failed. " + ex;
            logger.error(s);
            return s;
        }

    }

    @Override
    public String handleRedirectQuery(String authorizationCode, UUID id) {
        FhirClient client = this.clients.get(id);
        CompletableFuture<String> completableFuture = this.redirectResults.get(id);

        if (client == null || completableFuture == null) {
            logger.info("unrecognized id");
            return "unrecognized state: " + id;
        }

        try {
            client.requestAccess(authorizationCode);
            
            if (!client.hasAccess()) {
                String s = "authorization code was not accepted";
                logger.info(s);
                return s;
            }

            try {
                Patient p = client.getPatient();

                logger.info("Patient name: " + p.getNameFirstRep().getNameAsSingleString());
                
                completableFuture.complete(client.accessData().toString() + "\n\n\n First *ever* FHIR query.... Patient name: " + p.getNameFirstRep().getNameAsSingleString());
            } catch (IllegalStateException ex) {
                String s = "client could not get the patient. " + ex;
                logger.error(s);
                completableFuture.complete(s);
            }

            
        } catch (IOException ex) {
            logger.info("requestAccessToken exception: " + ex);
            return ex.getMessage();
        }

        return "everything seems to be ok thus far once again";

    }

}
