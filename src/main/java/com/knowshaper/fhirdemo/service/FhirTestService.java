/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.knowshaper.fhirdemo.service;

import java.util.UUID;

/**
 *
 * @author kris
 */
public interface FhirTestService {

    public String handleLaunchQuery(String iss, String launch_token);

    public String handleRedirectQuery(String code, UUID state);
}
