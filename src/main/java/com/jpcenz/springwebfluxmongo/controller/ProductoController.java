package com.jpcenz.springwebfluxmongo.controller;

import com.jpcenz.springwebfluxmongo.models.document.Categoria;
import com.jpcenz.springwebfluxmongo.models.document.Producto;
import com.jpcenz.springwebfluxmongo.service.ProductoService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.thymeleaf.spring6.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Controller
@SessionAttributes({"producto","titulo"})
public class ProductoController {

    @Value("${config.uploads.path}")
    private String path;

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    @ModelAttribute("categorias")
    public Flux<Categoria> categorias(){
        return service.findAllCategoria();
    }

    @GetMapping({"/listar","/"})
    public String listar(Model model){
        Flux<Producto> productos = service.findAllConNombreUpperCase();
        productos.subscribe(producto -> log.info(producto.getNombre()));
        model.addAttribute("productos",productos);
        model.addAttribute("titulo","Listado de productos");
        return "listar";
    }

    @GetMapping("/form")
    public Mono<String> crear(Model model){
        model.addAttribute("producto",new Producto());
        model.addAttribute("titulo","Formulario de producto");
        model.addAttribute("boton","Crear");
        return Mono.just("form")
                .doOnNext(s -> log.info("Cargando el formulario"));
    }

    @PostMapping("/form")
    public Mono<String> guardar(@Valid Producto producto, BindingResult result, Model model,
                                @RequestPart(name = "file") FilePart file,  SessionStatus status){
//        Use SessionStatus to mark the current handler's session processing as complete.
//        This is typically done after saving or updating an entity to clear the session attributes.
        if (result.hasErrors()){
            log.error("Error en el formulario: "+result.getAllErrors());
            model.addAttribute("titulo","Error en el formulario");
            return Mono.just("form");
        }

        // Check if the file is an image
        String contentType = Objects.requireNonNull(file.headers().getContentType()).toString();
        if (!file.filename().isEmpty() && !contentType.startsWith("image/")) {
            log.error("El archivo no es una imagen: " + contentType);
            model.addAttribute("titulo", "Error en el formulario");
            model.addAttribute("error", "El archivo debe ser una imagen");

            return Mono.just("form");
        }

        status.setComplete();
        Mono<Categoria> categoria = service.findCategoriaById(producto.getCategoria().getId());

        return categoria.flatMap(c -> {
                    producto.setCategoria(c);
                    if (producto.getId() == null){
                        producto.setCreateAt(new java.util.Date());
                    }
                    if (!file.filename().isEmpty()){
                        producto.setFoto(UUID.randomUUID() +"-"+file.filename()
                                .replace(" ","")
                                .replace(":","")
                                .replace("\\",""));
                    }
                    return  service.save(producto);
                })
                .doOnError(t -> log.error(t.getMessage()))
                .doOnNext(p -> log.info("Producto guardado: "+p.getNombre()))
                .flatMap(p->{
                    if (!file.filename().isEmpty()){
                        return file.transferTo(new File(path+p.getFoto()));
                    }
                    return Mono.empty();
                })
                .thenReturn("redirect:/listar")
                .doOnError(t -> log.error(t.getLocalizedMessage()));
    }

    @GetMapping("/form/{id}")
    public Mono<String> editar(@PathVariable String id,Model model){

        Mono<Producto> producto =service.findById(id).doOnNext(p -> {

            log.info("Producto encontrado: ");
        }).defaultIfEmpty(new Producto())
                .onErrorResume(ex -> {
            log.error("Error al consultar el producto: "+ex.getMessage());
            return Mono.just(new Producto());
        });
        model.addAttribute("producto",producto);
        model.addAttribute("titulo","Editar producto");
        model.addAttribute("boton","Editar");
        return Mono.just("form").doOnNext(s -> log.info("Cargando el formulario para editar"));
    }

    @GetMapping("/ver/{id}")
    public Mono<String> ver(@PathVariable String id,Model model){
        return service.findById(id).doOnNext(p -> {
            model.addAttribute("producto",p);
            model.addAttribute("titulo","Detalle del producto");
        }).defaultIfEmpty(new Producto())
                .flatMap(p -> {
                    if (p.getId() == null){
                        return Mono.error(new InterruptedException("No existe el producto"));
                    }
                    return Mono.just(p);
                }).then(Mono.just("ver"))
                .onErrorResume(ex -> {
                    log.error("Error al consultar el producto: "+ex.getMessage());
                    return Mono.just("redirect:/listar?error=No+existe+el+producto");
                });
    }

    @GetMapping("/uploads/img/{nombreFoto:.+}")
    public Mono<ResponseEntity<Resource>> verFoto(@PathVariable(name = "nombreFoto") String nombreFoto) throws MalformedURLException {
        Path ruta = Paths.get(path).resolve(nombreFoto).toAbsolutePath();
        Resource imagen = new UrlResource(ruta.toUri());

        return Mono.just(ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+imagen.getFilename()+"\"")
                .body(imagen));
    }

    @GetMapping("/form-v2/{id}")
    public Mono<String> editarV2(@PathVariable String id,Model model){

      return service.findById(id).doOnNext(p -> {
          log.info("Producto encontrado: "+p.getId());
          model.addAttribute("producto",p);
          model.addAttribute("titulo","Editar producto");
          model.addAttribute("boton","Editar");
                }).defaultIfEmpty(new Producto()).doOnNext(s -> log.info("Cargando el formulario para editar"))
              .flatMap(p -> {
                  if(p.getId() == null){
                      return Mono.error(new InterruptedException("No existe el producto"));
                  }
                  return Mono.just(p);
              })
              .then(Mono.just("form")
              .onErrorResume(ex -> {
                  log.error("Error al consultar el producto: "+ex.getMessage());
                  return Mono.just("redirect:/listar?error=No+existe+el+producto");
              }))   ;

    }

    @GetMapping("/eliminar/{id}")
    public Mono<String> eliminar(@PathVariable String id){
        return service.findById(id).defaultIfEmpty(new Producto())
                .flatMap(p -> {
                    if (p.getId() == null){
                        return Mono.error(new InterruptedException("No existe el producto a eliminar"));
                    }
                    return Mono.just(p);
                }).flatMap(p -> {
                    log.info("Eliminando el producto: "+p.getId());
                    return service.delete(p);
                }).then(Mono.just("redirect:/listar"))
                .onErrorResume(ex -> {
                    log.error("Error al eliminar el producto: "+ex.getMessage());
                    return Mono.just("redirect:/listar?error=No+existe+el+producto+a+eliminar");
                });
    }






    @GetMapping({"/listar-datadriver"})
    public String listarDataDriver(Model model){
        Flux<Producto> productos = service.findAllConNombreUpperCase()
                .delayElements(java.time.Duration.ofSeconds(1));

        productos.subscribe(producto -> log.info(producto.getNombre()));
        model.addAttribute("productos",new ReactiveDataDriverContextVariable(productos,1));
        model.addAttribute("titulo","Listado de productos");
        return "listar";
    }

    @GetMapping({"/listar-full"})
    public String listarFull(Model model){
        Flux<Producto> productos = service.findAll().map(producto -> {
            producto.setNombre(producto.getNombre().toUpperCase());
            return producto;
        }).repeat(10000);

        model.addAttribute("productos",new ReactiveDataDriverContextVariable(productos,1));
        model.addAttribute("titulo","Listado de productos");
        return "listar";
    }

    @GetMapping({"/listar-chunked"})
    public String listarChunked(Model model){
        Flux<Producto> productos = service.findAllConNombreeUpperCaseRepeat();

        model.addAttribute("productos",new ReactiveDataDriverContextVariable(productos,1));
        model.addAttribute("titulo","Listado de productos");
        return "listar-chunked";
    }
}
