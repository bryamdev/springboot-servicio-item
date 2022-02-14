package com.micro.springboot.app.item;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

//Ribbon permite la administracion del balanceo de cargas
//Feign cliente para el consumo de API Rest en el servicio actual
//EurekaClient para habilitar este servicio como cliente del servidor Eureka
@EnableEurekaClient
//@RibbonClient(name = "servicio-productos")
@EnableFeignClients
@SpringBootApplication
public class SpringbootServicioItemApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootServicioItemApplication.class, args);
	}

}
