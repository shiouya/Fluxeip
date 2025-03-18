package com.example.fluxeip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class FluxeipApplication {

	public static void main(String[] args) {
		
		Dotenv dotenv = Dotenv.load();
		String username = dotenv.get("username.email");
        String password = dotenv.get("password");
        System.setProperty("username", username);
        System.setProperty("password", password);
		SpringApplication.run(FluxeipApplication.class, args);
	}

}
