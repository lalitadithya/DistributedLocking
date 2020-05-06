package com.lalitadithya;

import com.hazelcast.config.Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public Config config() {
        Config config = new Config();
        config.getNetworkConfig().setPortAutoIncrement(true);
        config.getCPSubsystemConfig().setCPMemberCount(3);
        //config.getNetworkConfig().getKubernetesConfig().setEnabled(true);
        //config.getNetworkConfig().getKubernetesConfig().setProperty("service-name","hazelcast-cluster");
        return config;
    }
}