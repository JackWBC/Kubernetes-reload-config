package com.baicheng.Kubernetesreloadconfig.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author baicheng
 * @description
 * @create 2019-08-29 20:21
 */
@RestController
public class LiveController {

    @GetMapping("/")
    public String live(){
        return "success";
    }
}
