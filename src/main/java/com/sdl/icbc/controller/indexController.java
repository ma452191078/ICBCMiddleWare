package com.sdl.icbc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by majingyuan on 2017/5/29.
 *
 */
@RestController
public class indexController {
    @RequestMapping("/")
    public String helloHtml(){

        return "ICBCMiddleWare is running.";
    }
}
