package com.baicheng.Kubernetesreloadconfig.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author baicheng
 * @description
 * @create 2019-08-28 17:04
 */
@Configuration
@ConfigurationProperties(prefix = "bean")
@Data
public class MyConfig {

    private String message = "a message that can be changed live";

}
