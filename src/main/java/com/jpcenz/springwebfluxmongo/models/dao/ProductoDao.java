package com.jpcenz.springwebfluxmongo.models.dao;


import com.jpcenz.springwebfluxmongo.models.document.Producto;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ProductoDao extends ReactiveMongoRepository<Producto,String> {
}
