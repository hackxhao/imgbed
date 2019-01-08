package com.webug.imgbed.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test")
public class HelloWorld {

    @RequestMapping("/123")
    public String test(){
        return "success";
    }
}
