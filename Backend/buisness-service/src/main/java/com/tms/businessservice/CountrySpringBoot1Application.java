package com.tms.businessservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class CountrySpringBoot1Application {

	public static void main(String[] args) {
		SpringApplication.run(CountrySpringBoot1Application.class, args);
	}

}
