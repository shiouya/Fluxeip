package com.example.fluxeip.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.fluxeip.jwt.JsonWebTokenInterceptor;

@Configuration
public class SpringBootConfig implements WebMvcConfigurer {

	@Autowired
	private JsonWebTokenInterceptor jsonWebTokenInterceptor;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/secure/ajax/login");
		registry.addMapping("/department/find");
		registry.addMapping("/position/find");
		registry.addMapping("/employee/create").allowedMethods("POST");
		registry.addMapping("/api/clock/*").allowedMethods("POST");
		
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {

//		registry.addInterceptor(jsonWebTokenInterceptor)
//				.addPathPatterns("/pages/ajax/products/**");
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
