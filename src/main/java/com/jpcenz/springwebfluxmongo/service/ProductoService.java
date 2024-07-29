package com.jpcenz.springwebfluxmongo.service;

import com.jpcenz.springwebfluxmongo.models.document.Categoria;
import com.jpcenz.springwebfluxmongo.models.document.Producto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductoService {

    public Flux<Producto> findAll();

    public Flux<Producto> findAllConNombreUpperCase();
    public Flux<Producto> findAllConNombreeUpperCaseRepeat();

    public Mono<Producto> findById(String id);

    public Mono<Producto> save(Producto producto);

    public Mono<Void> delete(Producto producto);
    public Flux<Categoria> findAllCategoria();
    public Mono<Categoria> findCategoriaById(String id);
    public Mono<Categoria> saveCategoria(Categoria categoria);
    public Flux<Categoria> findCategoriaByNombre(String nombre);
}
