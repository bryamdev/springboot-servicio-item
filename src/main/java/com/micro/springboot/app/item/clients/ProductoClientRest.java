package com.micro.springboot.app.item.clients;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.micro.springboot.app.item.models.Producto;

//@FeignClient(name = "servicio-productos", url = "localhost:8001") <- para cliente feign
@FeignClient(name = "servicio-productos")
public interface ProductoClientRest {

	@GetMapping("/listar")
	public List<Producto> listar();
	
	@GetMapping("/ver/{id}")
	public Producto detalle(@PathVariable Long id);	
	
}
