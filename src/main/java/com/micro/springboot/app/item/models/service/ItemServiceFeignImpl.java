package com.micro.springboot.app.item.models.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.micro.springboot.app.item.clients.ProductoClientRest;
import com.micro.springboot.app.item.models.Item;
import com.micro.springboot.app.item.models.Producto;

@Service("serviceFeign")
public class ItemServiceFeignImpl implements ItemService {

	@Autowired
	private ProductoClientRest clienteFeign;
	
	@Override
	public List<Item> findAll() {
		return clienteFeign.listar()
				.stream()
				.map( prod -> new Item( prod, 1) )
				.collect(Collectors.toList());
	}

	@Override
	public Item findById(Long id, Integer cantidad) {
		Producto producto = clienteFeign.detalle(id);
		return new Item(producto, cantidad);
	}

}
