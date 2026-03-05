package com.kkomo.kkomo_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KkomoApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(KkomoApiApplication.class, args);
	}

}
