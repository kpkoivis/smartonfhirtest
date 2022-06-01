/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.knowshaper.fhirdemo.controller;

import com.knowshaper.fhirdemo.service.FhirTestService;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author kris
 */
@Controller
public class MainController {

    Logger logger = LoggerFactory.getLogger(MainController.class);

    @Autowired
    private FhirTestService fhirService;

    @GetMapping(value = "/", params = {"iss", "launch"})
    @ResponseBody
    public String launchQuery(@RequestParam String iss, @RequestParam String launch) {
        logger.info("This is launchQuery. iss: " + iss + " launch: " + launch);
        String result = this.fhirService.handleLaunchQuery(iss, launch);
        return "<pre>" + result + "</pre>";
    }

    @GetMapping(value = "/", params = {"code", "state"})
    @ResponseBody
    public String handleRedirectQuery(@RequestParam String code, @RequestParam UUID state) {
        logger.info("This is handleRedirectQuery code: " + code + " state: " + state);
        String result = this.fhirService.handleRedirectQuery(code, state);
        return "This is hello from handleRedirectQuery!";
    }
}
