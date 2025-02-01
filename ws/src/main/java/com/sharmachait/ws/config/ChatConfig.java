package com.sharmachait.ws.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.sharmachait.ws.repository", entityManagerFactoryRef = "chatEntityManagerFactory", transactionManagerRef = "chatTransactionManager")
public class ChatConfig {
}
