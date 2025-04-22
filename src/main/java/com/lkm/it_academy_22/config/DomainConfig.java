package com.lkm.it_academy_22.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EntityScan("com.lkm.it_academy_22.domain")
@EnableJpaRepositories("com.lkm.it_academy_22.repos")
@EnableTransactionManagement
public class DomainConfig {
}
