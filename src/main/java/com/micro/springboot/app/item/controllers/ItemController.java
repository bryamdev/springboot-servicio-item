package com.micro.springboot.app.item.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.micro.springboot.app.item.models.Item;
import com.micro.springboot.app.item.models.Producto;
import com.micro.springboot.app.item.models.service.ItemService;

@RestController
public class ItemController {

	@Autowired
	private CircuitBreakerFactory cbFactory;
	
	@Autowired
	@Qualifier("serviceFeign")
	private ItemService itemService;
	
	@GetMapping("/listar")
	public List<Item> listar(@RequestParam(name = "usuario", required = false) String usuario, @RequestHeader(name = "token-request", required = false) String token){
		System.out.println("parametro: " + usuario);
		System.out.println("header: " + token);
		return itemService.findAll();
	}
	
	//HystrixCommand permite definir el metodo que procesara un fallo capturado por Hystrix al intentar realizar comunicación con el microservicio
	//@HystrixCommand(fallbackMethod = "metodoAlternativo")
	@GetMapping("/ver/{id}/cantidad/{cantidad}")
	public Item verItem(@PathVariable Long id, @PathVariable Integer cantidad){
		//con metodo create, se crea un nuevo circuito con el nombre 'items'
		//con run intenta la comuniacion al microservicio (primer parametro)
		//Si hay un error en comunicacion ejecuta una excepcion/camino alternativo (segundo parametro)
		return cbFactory.create("items")
				.run( ()-> itemService.findById(id, cantidad), e -> metodoAlternativo(id, cantidad));
	}
	
	public Item metodoAlternativo(Long id, Integer cantidad){
		
		Item item = new Item();
		Producto producto = new Producto();
		
		item.setCantidad(cantidad);
		producto.setId(id);
		producto.setNombre("Salvavidas");
		producto.setPrecio(11.11);
		
		item.setProducto(producto);
		
		return item;
		
	}
	
}
