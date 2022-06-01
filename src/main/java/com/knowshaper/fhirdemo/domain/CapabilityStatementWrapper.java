package com.knowshaper.fhirdemo.domain;

import org.hl7.fhir.r4.model.CapabilityStatement;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kris
 */
public class CapabilityStatementWrapper {
    
    Logger logger = LoggerFactory.getLogger(CapabilityStatementWrapper.class);

    private final CapabilityStatement capsStatement;

    public CapabilityStatementWrapper(CapabilityStatement capsStatement) {
        this.capsStatement = capsStatement;
    }

    public CapabilityStatement.CapabilityStatementRestComponent getRestComponentFirstRep() {
        return this.capsStatement.getRestFirstRep();
    }

    public CapabilityStatement.CapabilityStatementRestSecurityComponent getRestComponentFirstRepSecurityComponent() {
        CapabilityStatement.CapabilityStatementRestComponent rest_component = this.getRestComponentFirstRep();
        if (rest_component != null) {
            return rest_component.getSecurity();
        } else {
            return null;
        }
    }

    public Extension getRestComponentFirstRepSecurityComponentExtensionByUrl(String url) {
        CapabilityStatement.CapabilityStatementRestSecurityComponent security_component = this.getRestComponentFirstRepSecurityComponent();
        if (security_component != null) {
            return security_component.getExtensionByUrl(url);
        } else {
            return null;
        }
    }

    public Extension getRestComponentFirstRepSecurityComponentOAuthExtension() {
        return getRestComponentFirstRepSecurityComponentExtensionByUrl("http://fhir-registry.smarthealthit.org/StructureDefinition/oauth-uris");

    }

    public String getOauthAuthorizationEndpointUrlFirstRep() {
        Extension e = getRestComponentFirstRepSecurityComponentOAuthExtension();
        if (e != null) {
            Extension authorize_extension = e.getExtensionByUrl("authorize");
            Type authorize_endpoint = authorize_extension.getValue();
            if (authorize_endpoint != null) {
                return authorize_endpoint.primitiveValue();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public String getOauthTokenEndpointUrlFirstRep() {
        Extension e = getRestComponentFirstRepSecurityComponentOAuthExtension();
        if (e != null) {
            Extension authorize_extension = e.getExtensionByUrl("token");
            Type authorize_endpoint = authorize_extension.getValue();
            if (authorize_endpoint != null) {
                return authorize_endpoint.primitiveValue();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public CapabilityStatement.CapabilityStatementImplementationComponent getImplementationComponent() {
        return this.capsStatement.getImplementation();
    }
    
    public String getImplementationComponentUrl() {
        String url = this.getImplementationComponent().getUrl();
        logger.info("implementation component url: " + url);
        return url;
    }
/*
    public String toJSONString(FhirContext ctx) {
        IParser jsonParser = ctx.newJsonParser();
        jsonParser.setPrettyPrint(true);
        String encoded = jsonParser.encodeResourceToString(this.capsStatement);
        return encoded;
    }
*/

}
