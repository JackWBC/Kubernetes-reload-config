package com.baicheng.Kubernetesreloadconfig.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author baicheng
 * @description
 * @create 2019-08-28 17:09
 */
@Configuration
@ConfigurationProperties(prefix = "dummy")
@Data
public class DummyConfig {

    private String message = "this is a dummy message";
}
