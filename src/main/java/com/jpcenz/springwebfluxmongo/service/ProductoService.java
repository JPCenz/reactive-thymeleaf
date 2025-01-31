package com.jpcenz.springwebfluxmongo.service;

import com.jpcenz.springwebfluxmongo.models.document.Categoria;
import com.jpcenz.springwebfluxmongo.models.document.Producto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductoService {

    Flux<Producto> findAll();

    Flux<Producto> findAllConNombreUpperCase();
    Flux<Producto> findAllConNombreeUpperCaseRepeat();

    Mono<Producto> findById(String id);

    Mono<Producto> save(Producto producto);

    Mono<Void> delete(Producto producto);
    Flux<Categoria> findAllCategoria();
    Mono<Categoria> findCategoriaById(String id);
    Mono<Categoria> saveCategoria(Categoria categoria);
    Flux<Categoria> findCategoriaByNombre(String nombre);
}
