package com.cursosudemy.microservices.mscloudgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableEurekaClient
@EnableDiscoveryClient
public class MscloudgatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(MscloudgatewayApplication.class, args);
	}

	@Bean
	public RouteLocator routes(RouteLocatorBuilder builder){
		return builder
				// A cada novo microserviço criado, deve-se registra-lo aqui para fazer o balacemento de carga.
				.routes()
				/*
				* Toda chamada feita no /clientes através do gateway será redirecionado para o load balancer
				* e para o micro-serviço de clientes (msclientes).
				* */
				.route(r -> r.path("/clientes/**").uri("lb://msclientes"))
				.build();
	}

}
