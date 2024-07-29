package com.jpcenz.springwebfluxmongo.models.dao;

import com.jpcenz.springwebfluxmongo.models.document.Categoria;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface CategoriaDao extends ReactiveMongoRepository<Categoria,String> {
    public Flux<Categoria> findAllByNombre(String nombre);
}
