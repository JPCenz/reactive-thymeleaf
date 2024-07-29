package com.jpcenz.springwebfluxmongo.service.impl;

import com.jpcenz.springwebfluxmongo.models.dao.CategoriaDao;
import com.jpcenz.springwebfluxmongo.models.dao.ProductoDao;
import com.jpcenz.springwebfluxmongo.models.document.Categoria;
import com.jpcenz.springwebfluxmongo.models.document.Producto;
import com.jpcenz.springwebfluxmongo.service.ProductoService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductoServiceImpl implements ProductoService {
    private final ProductoDao dao;
    private final CategoriaDao categoriaDao;

    public ProductoServiceImpl(ProductoDao dao, CategoriaDao categoriaDao) {
        this.dao = dao;
        this.categoriaDao = categoriaDao;
    }

    @Override
    public Flux<Producto> findAll() {
        return dao.findAll();
    }

    @Override
    public Flux<Producto> findAllConNombreUpperCase() {

        return dao.findAll().map(producto -> {
            producto.setNombre(producto.getNombre().toUpperCase());
            return producto;
        });
    }

    @Override
    public Flux<Producto> findAllConNombreeUpperCaseRepeat() {
        return findAllConNombreUpperCase().repeat(5000);
    }

    @Override
    public Mono<Producto> findById(String id) {
        return dao.findById(id);
    }

    @Override
    public Mono<Producto> save(Producto producto) {
        return dao.save(producto);
    }

    @Override
    public Mono<Void> delete(Producto producto) {
        return dao.delete(producto);
    }

    @Override
    public Flux<Categoria> findAllCategoria() {
        return categoriaDao.findAll();
    }

    @Override
    public Mono<Categoria> findCategoriaById(String id) {
        return categoriaDao.findById(id);
    }

    @Override
    public Mono<Categoria> saveCategoria(Categoria categoria) {
        return categoriaDao.save(categoria);
    }

    @Override
    public Flux<Categoria> findCategoriaByNombre(String nombre) {
        return categoriaDao.findAllByNombre(nombre);
    }
}
