package com.icw.esign;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.retry.annotation.EnableRetry;

import java.util.TimeZone;

@SpringBootApplication
@EnableRetry
@ComponentScan(basePackages = {"com.icw.esign", "com.icw.esign.config"})
public class EsignApiApplication {
	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		SpringApplication.run(EsignApiApplication.class, args);
	}
}
