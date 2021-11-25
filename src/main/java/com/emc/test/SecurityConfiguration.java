package com.emc.test;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

    @Configuration
    @EnableWebSecurity
    @EnableAutoConfiguration(exclude = { 
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class 
        })
    public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity httpSecurity) throws Exception {
            //httpSecurity.authorizeRequests().anyRequest().authenticated();
            //httpSecurity.headers().cacheControl();
        }
    }