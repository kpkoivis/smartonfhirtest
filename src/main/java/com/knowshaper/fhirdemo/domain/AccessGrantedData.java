/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.knowshaper.fhirdemo.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author kris
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessGrantedData {
    @Getter @Setter
    private String access_token;
    @Getter @Setter
    private String token_type;
    @Getter @Setter
    private String expires_in;
    @Getter @Setter
    private String scope;
    @Getter @Setter
    private String is_token;
    @Getter @Setter
    @JsonProperty("__epic.dstu2.patient")
    private String epic_dstu2_patient;
    @Getter @Setter
    private String patient;
    @Getter @Setter
    private String appointment;
    @Getter @Setter
    private String encounter;
    @Getter @Setter
    private String location;
    @Getter @Setter
    private String loginDepartment;
    @Getter @Setter
    private String state;
    @Getter @Setter
    private String dob;
    @Getter @Setter
    private String need_patient_banner; 
    @Getter @Setter
    private String smart_style_url; 
    @Getter @Setter
    private String user; 
    
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("access_token: ").append(this.access_token).append("\n");
        b.append("token_type: ").append(this.token_type).append("\n");
        b.append("expires_in: ").append(this.expires_in).append("\n");
        b.append("scope: ").append(this.scope).append("\n");
        b.append("is_token: ").append(this.is_token).append("\n");
        b.append("patient: ").append(this.patient).append("\n");
        b.append("__epic.dstu2.patient: ").append(this.epic_dstu2_patient).append("\n");
        b.append("appointment: ").append(this.appointment).append("\n");
        b.append("encounter: ").append(this.encounter).append("\n");
        b.append("location: ").append(this.location).append("\n");
        b.append("loginDepartment: ").append(this.loginDepartment).append("\n");
        b.append("state: ").append(this.state).append("\n");
        b.append("dob: ").append(this.dob).append("\n");
        b.append("need_patient_banner: ").append(this.need_patient_banner).append("\n");
        b.append("smart_style_url: ").append(this.smart_style_url).append("\n");
        b.append("user: ").append(this.user).append("\n");
        
        return b.toString();
    }
}
