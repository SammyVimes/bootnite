package com.github.sammyvimes.bootnite;

import com.github.sammyvimes.bootnite.config.IgniteConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = {
		"com.github.sammyvimes.bootnite.config",
		"com.github.sammyvimes.bootnite.repo",
		"com.github.sammyvimes.bootnite.service",
		"com.github.sammyvimes.bootnite.controller"
})
@EnableDiscoveryClient
public class BootniteApplication {

	public static void main(String[] args) {
		SpringApplication.run(BootniteApplication.class, args);
	}

}
