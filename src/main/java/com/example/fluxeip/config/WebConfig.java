package com.example.fluxeip.config;

import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
    	String projectRoot = System.getProperty("user.dir");
        String uploadDir = Paths.get(projectRoot, "uploads/images/").toString();

        registry.addResourceHandler("/uploads/images/**")
                .addResourceLocations("file:" + uploadDir + "/");
    }
    
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOrigins("http://localhost:5173")  // 允許的前端地址
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 允許的 HTTP 方法
//                .allowedHeaders("*")  // 允許所有標頭
//                .allowCredentials(true);  // 允許攜帶憑證（如 Cookie）
//    }
}

