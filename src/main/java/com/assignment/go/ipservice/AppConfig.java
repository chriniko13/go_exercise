package com.assignment.go.ipservice;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration


@EnableJpaRepositories(basePackages = { "com.assignment.go.ipservice.*" })

@EntityScan(basePackages = { "com.assignment.go.ipservice.*" })

public class AppConfig {




}
