package com.micro.springboot.app.item.models.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.micro.springboot.app.item.models.Item;
import com.micro.springboot.app.item.models.Producto;

@Service("serviceRestTemplate")
@Primary
public class ItemServiceImpl implements ItemService {

	@Autowired
	private RestTemplate clienteRest;	
	
	@Override
	public List<Item> findAll() {
		//List<Producto> productos = Arrays.asList(clienteRest.getForObject("http://localhost:8001/listar", Producto[].class)); //Solo RestTemplate 
		List<Producto> productos = Arrays.asList(clienteRest.getForObject("http://servicio-productos/listar", Producto[].class)); //RestTemplate con Ribbon
		
		return productos
				.stream()
				.map( prod -> new Item(prod, 3) )
				.collect(Collectors.toList());
	}

	@Override
	public Item findById(Long id, Integer cantidad) {
		
		Map<String, String> pathVariables = new HashMap<>();
		pathVariables.put("id", id.toString());
		//Producto producto = clienteRest.getForObject("http://localhost:8001/ver/{id}", Producto.class, pathVariables);//Solo RestTemplate
		Producto producto = clienteRest.getForObject("http://servicio-productos/ver/{id}", Producto.class, pathVariables); //RestTemplate con Ribbon
		
		return new Item(producto, cantidad);
	}

	@Override
	public Producto save(Producto producto) {
		//Entidad HTTP que puede representar un requesto o un reseponse
		HttpEntity<Producto> body = new HttpEntity<Producto>(producto);		
		ResponseEntity<Producto> response = clienteRest.exchange("http://servicio-productos/crear", HttpMethod.POST, 
				body, Producto.class);
		return response.getBody();
	}

	@Override
	public Producto update(Producto producto, Long id) {
		Map<String, String> pathVariables = new HashMap<>();
		pathVariables.put("id", id.toString());
		
		HttpEntity<Producto> body = new HttpEntity<Producto>(producto);
		ResponseEntity<Producto> response = clienteRest.exchange("http://servicio-productos/editar/{id}", HttpMethod.PUT,
				body, Producto.class, pathVariables);
		return response.getBody();
	}

	@Override
	public void delete(Long id) {
		Map<String, String> pathVariables = new HashMap<>();
		pathVariables.put("id", id.toString());
		
		clienteRest.delete("http://servicio-productos/eliminar/{id}", pathVariables);
	}

}
