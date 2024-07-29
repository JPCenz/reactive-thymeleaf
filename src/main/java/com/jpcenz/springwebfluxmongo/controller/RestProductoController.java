package com.jpcenz.springwebfluxmongo.controller;

import com.jpcenz.springwebfluxmongo.models.document.Producto;
import com.jpcenz.springwebfluxmongo.service.ProductoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/productos")
@Slf4j
public class RestProductoController {

    private final ProductoService service;

    public RestProductoController(ProductoService service) {
        this.service = service;
    }

    @GetMapping
    public Flux<Producto> index(){
        return service.findAll().map(producto -> {
            producto.setNombre(producto.getNombre().toUpperCase());
            return producto;
        }).doOnNext(prod -> log.info(prod.getNombre()));
    }

    @GetMapping("{id}")
    public Mono<Producto> getProducto(@PathVariable String id){
        //var producto = dao.findById(id);
        Flux<Producto> productos = service.findAll();
        return productos.filter(p -> p.getId().equals(id)).next()
        .doOnNext(prod -> log.info(prod.getNombre()));
    }
}
