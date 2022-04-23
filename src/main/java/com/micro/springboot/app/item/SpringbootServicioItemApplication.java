package com.micro.springboot.app.item;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

//Ribbon permite la administracion del balanceo de cargas
//Feign cliente para el consumo de API Rest en el servicio actual
//EurekaClient para habilitar este servicio como cliente del servidor Eureka
//EnableCirucitBraker era utilizado para Hystrix
//EnableAutoConfiguration se deshabilita la autoconfig del datasource de JPA

//@EnableCircuitBreaker
@EnableEurekaClient
//@RibbonClient(name = "servicio-productos")
@EnableFeignClients
@SpringBootApplication
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public class SpringbootServicioItemApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootServicioItemApplication.class, args);
	}

}
