package com.elzocodeur.campusmaster.infrastructure.config.database;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "com.elzocodeur.campusmaster.infrastructure.persistence.repository")
@EnableTransactionManagement
public class JpaConfig {
}
