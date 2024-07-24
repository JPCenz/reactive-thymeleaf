package com.jpcenz.springwebfluxmongo.controller;

import com.jpcenz.springwebfluxmongo.models.dao.ProductoDao;
import com.jpcenz.springwebfluxmongo.models.document.Producto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.thymeleaf.spring6.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;

@Slf4j
@Controller
public class ProductoController {

    private ProductoDao dao;

    public ProductoController(ProductoDao dao) {
        this.dao = dao;
    }

    @GetMapping({"/listar","/"})
    public String listar(Model model){
        Flux<Producto> productos = dao.findAll().map(producto -> {
            producto.setNombre(producto.getNombre().toUpperCase());
            return producto;
        });
        productos.subscribe(producto -> log.info(producto.getNombre()));
        model.addAttribute("productos",productos);
        model.addAttribute("titulo","Listado de productos");
        return "listar";
    }

    @GetMapping({"/listar-datadriver","/"})
    public String listarDataDriver(Model model){
        Flux<Producto> productos = dao.findAll().map(producto -> {
            producto.setNombre(producto.getNombre().toUpperCase());
            return producto;
        }).delayElements(java.time.Duration.ofSeconds(1));
        productos.subscribe(producto -> log.info(producto.getNombre()));
        model.addAttribute("productos",new ReactiveDataDriverContextVariable(productos,1));
        model.addAttribute("titulo","Listado de productos");
        return "listar";
    }

    @GetMapping({"/listar-full","/"})
    public String listarFull(Model model){
        Flux<Producto> productos = dao.findAll().map(producto -> {
            producto.setNombre(producto.getNombre().toUpperCase());
            return producto;
        }).repeat(10000);

        model.addAttribute("productos",new ReactiveDataDriverContextVariable(productos,1));
        model.addAttribute("titulo","Listado de productos");
        return "listar";
    }

    @GetMapping({"/listar-chunked","/"})
    public String listarChunked(Model model){
        Flux<Producto> productos = dao.findAll().map(producto -> {
            producto.setNombre(producto.getNombre().toUpperCase());
            return producto;
        }).repeat(10000);

        model.addAttribute("productos",new ReactiveDataDriverContextVariable(productos,1));
        model.addAttribute("titulo","Listado de productos");
        return "listar-chunked";
    }
}
