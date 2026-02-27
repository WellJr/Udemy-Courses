package com.pieropan.analisecredito;

import com.pieropan.analisecredito.domain.Proposta;
import com.pieropan.analisecredito.service.AnaliseCreditoService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AnalisecreditoApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnalisecreditoApplication.class, args);
	}

	@Autowired
	private AnaliseCreditoService analiseCreditoService;

	@Bean
	public CommandLineRunner commandLineRunner() {
		return args -> {
			analiseCreditoService.analisar(new Proposta());
		};
	}

}
