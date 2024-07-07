package com.gestionTFT.GestionTFT;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.gestionTFT.GestionTFT.repository")
@EntityScan(basePackages = "com.gestionTFT.GestionTFT.entity")
public class GestionTftApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestionTftApplication.class, args);
	}

}
