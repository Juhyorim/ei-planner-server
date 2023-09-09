package com.kihyaa.Eiplanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class EiPlannerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EiPlannerApplication.class, args);
	}

}
