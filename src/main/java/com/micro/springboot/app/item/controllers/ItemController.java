package com.micro.springboot.app.item.controllers;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.bulkhead.annotation.Bulkhead.Type;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;

@RestController
public class ItemController {

	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@SuppressWarnings("rawtypes")
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
				.run( ()-> itemService.findById(id, cantidad), e -> metodoAlternativo(id, cantidad, e));
	}
	
	//Detalle pero version anotada con CircuitBraker para crear el circuito
	//Con la anotacion solo se toma la configuracion hecha por archivo (properties o yml) no la hecha por beans
	@CircuitBreaker(name = "items", fallbackMethod = "metodoAlternativo")
	@GetMapping("/ver2/{id}/cantidad/{cantidad}")
	public Item verItem2(@PathVariable Long id, @PathVariable Integer cantidad){
		return itemService.findById(id, cantidad);
	}
	
	//Se debe envolver en una llamada futura asincrona 'CompletableFuture' para calcular tiempo de time out
	@Bulkhead(name = "items", type = Type.THREADPOOL)
	@CircuitBreaker(name = "items", fallbackMethod = "metodoAlternativo2")
	@TimeLimiter(name = "items")
	@GetMapping("/ver3/{id}/cantidad/{cantidad}")
	public CompletableFuture<Item>  verItem3(@PathVariable Long id, @PathVariable Integer cantidad){
		return CompletableFuture.supplyAsync( () -> itemService.findById(id, cantidad));
	}
	
	public Item metodoAlternativo(Long id, Integer cantidad, Throwable e){
		
		log.info("Error: " + e.getMessage());
		
		Item item = new Item();
		Producto producto = new Producto();
		
		item.setCantidad(cantidad);
		producto.setId(id);
		producto.setNombre("Salvavidas");
		producto.setPrecio(11.11);
		
		item.setProducto(producto);
		
		return item;		
	}
	
	//Metodo alternativo con representacin futura para el metodo verItem3()
	public CompletableFuture<Item> metodoAlternativo2(Long id, Integer cantidad, Throwable e){
		
		log.info("Error: " + e.getMessage());
		
		Item item = new Item();
		Producto producto = new Producto();
		
		item.setCantidad(cantidad);
		producto.setId(id);
		producto.setNombre("Salvavidas");
		producto.setPrecio(11.11);
		
		item.setProducto(producto);
		
		return CompletableFuture.supplyAsync( () -> item );		
	}
	
}
