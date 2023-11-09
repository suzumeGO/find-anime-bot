package com.example.findanime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class FindAnimeApplication {

	public static void main(String[] args) {
		SpringApplication.run(FindAnimeApplication.class, args);
	}

}
