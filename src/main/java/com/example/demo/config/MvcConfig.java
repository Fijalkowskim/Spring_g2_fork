package com.example.demo.config;

import java.util.Objects;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.annotation.Nullable;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(@Nullable ViewControllerRegistry registry){
        if(Objects.nonNull(registry)){
            registry.addViewController("/login").setStatusCode(HttpStatus.UNAUTHORIZED).setViewName("login");
            registry.addViewController("/logout").setStatusCode(HttpStatus.UNAUTHORIZED).setViewName("logout");
            registry.addViewController("/").setStatusCode(HttpStatus.UNAUTHORIZED).setViewName("home");

        }
        
    }
    
}
