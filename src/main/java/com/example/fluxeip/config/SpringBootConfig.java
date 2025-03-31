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
		registry.addMapping("/position/find/**");
		registry.addMapping("/check/email/**");
		registry.addMapping("/check/identityCard/**");
		registry.addMapping("/check/phone/**");
		registry.addMapping("/employee/find");
		registry.addMapping("/employee/detail/**");
		registry.addMapping("/employee/update");
		registry.addMapping("/password/update");
		registry.addMapping("/forgot/password");
		registry.addMapping("/reset/password");
		registry.addMapping("/employee/create").allowedMethods("POST");
		registry.addMapping("/employee/search").allowedMethods("GET","POST");

		registry.addMapping("/bulletin/create").allowedMethods("POST");
		registry.addMapping("/bulletin/delete").allowedMethods("DELETE");
		registry.addMapping("/bulletin/update").allowedMethods("PUT");

		registry.addMapping("/api/clock/**").allowedMethods("POST");
		registry.addMapping("/api/attendancelogs/**");
		registry.addMapping("/api/leave-requests/**").allowedMethods("GET", "POST","PUT","DELETE");
		registry.addMapping("/api/approval/**").allowedMethods("GET", "POST","PUT","DELETE");
		registry.addMapping("/api/employee-approval-flows/**").allowedMethods("GET", "POST","DELETE");
		registry.addMapping("/api/work-adjustments/**").allowedMethods("GET", "POST","PUT","DELETE");
		registry.addMapping("/api/missing-punch/**").allowedMethods("GET", "POST","PUT","DELETE");
		registry.addMapping("/api/expense-requests/**").allowedMethods("GET", "POST","PUT","DELETE");
		registry.addMapping("/api/field-work/**").allowedMethods("GET", "POST","PUT");
		
		
		

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
