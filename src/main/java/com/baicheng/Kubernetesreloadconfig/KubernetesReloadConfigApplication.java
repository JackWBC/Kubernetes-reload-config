package com.baicheng.Kubernetesreloadconfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KubernetesReloadConfigApplication {

	public static void main(String[] args) {
		SpringApplication.run(KubernetesReloadConfigApplication.class, args);
	}

}
