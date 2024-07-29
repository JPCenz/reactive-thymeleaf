package com.jpcenz.springwebfluxmongo;

import com.jpcenz.springwebfluxmongo.models.document.Categoria;
import com.jpcenz.springwebfluxmongo.models.document.Producto;
import com.jpcenz.springwebfluxmongo.service.ProductoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Flux;


@SpringBootApplication
public class SpringWebfluxMongoApplication implements CommandLineRunner {
	private final ProductoService service;

	private final ReactiveMongoTemplate template;
	Logger log = LoggerFactory.getLogger(SpringWebfluxMongoApplication.class);

	public SpringWebfluxMongoApplication(ProductoService service, ReactiveMongoTemplate template) {
		this.service = service;
		this.template = template;
	}


	public static void main(String[] args) {
		SpringApplication.run(SpringWebfluxMongoApplication.class, args);
	}

	@Override
	public void run(String... args) {
		insertarDatos();

	}

	private void insertarDatos() {
		template.dropCollection("productos").subscribe();
		template.dropCollection("categorias").subscribe();
		//crear las categorias
		var electronico = Categoria.builder().nombre("electronico").codigo("001").build();
		var deporte = Categoria.builder().nombre("deporte").codigo("002").build();
		var computacion = Categoria.builder().nombre("computacion").codigo("003").build();
		var muebles = Categoria.builder().nombre("muebles").codigo("004").build();

		//crear nuevos productos con categoria usando builder de producto


		Flux.just(electronico, deporte, computacion, muebles)
				.flatMap(service::saveCategoria)
				.doOnNext(categoria -> log.info("Insert: " + categoria.getId() + " " + categoria.getNombre()))
						.thenMany(
								Flux.just(
											Producto.builder().nombre("TV panasonic").precio(456.77)
													.categoria(electronico).build(),
											Producto.builder().nombre("Sony camara")
													.precio(177.77).categoria(electronico).build(),
											Producto.builder().nombre("Sony notebook")
													.precio(177.77).categoria(computacion).build(),
											Producto.builder().nombre("Sony notebook")
													.precio(177.77).categoria(computacion).build()
										)
										.flatMap(producto -> {
											producto.setCreateAt(new java.util.Date());
											return service.save(producto);
										})
						)
				.subscribe(producto -> log.info("Insert: " + producto.getId() + " " + producto.getNombre()));
	}
}
