package com.jpcenz.springwebfluxmongo;

import com.jpcenz.springwebfluxmongo.models.dao.ProductoDao;
import com.jpcenz.springwebfluxmongo.models.document.Producto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Flux;


@SpringBootApplication
public class SpringWebfluxMongoApplication implements CommandLineRunner {
	private final ProductoDao dao;
	private final ReactiveMongoTemplate template;
	Logger log = LoggerFactory.getLogger(SpringWebfluxMongoApplication.class);

	public SpringWebfluxMongoApplication(ProductoDao dao, ReactiveMongoTemplate template) {
		this.dao = dao;
		this.template = template;
	}


	public static void main(String[] args) {
		SpringApplication.run(SpringWebfluxMongoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		template.dropCollection("productos").subscribe();
		Flux.just(new Producto("TV panasonic",456.77),
				new Producto("Sony camara",177.77),
				new Producto("Sony notebook",177.77),
				new Producto("Sony notebook",177.77))
				.flatMap(producto -> {
					producto.setCreateAt(new java.util.Date());
					return dao.save(producto);
				})
				.subscribe(producto -> log.info("Insert: "+producto.getId()+" "+producto.getNombre()));

	}
}
