# Spring WebFlux MongoDB Application

This is a Spring WebFlux application that uses MongoDB as the database. The application demonstrates CRUD operations on `Producto` and `Categoria` documents.

## Technologies Used

- Java
- Spring Boot
- Spring WebFlux
- Spring Data MongoDB
- Thymeleaf
- Maven

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6.3 or higher
- MongoDB

### Installation

1. Clone the repository:
    ```sh
    git clone <repository-url>
    cd <repository-directory>
    ```

2. Install the dependencies:
    ```sh
    mvn clean install
    ```

3. Run the application:
    ```sh
    mvn spring-boot:run
    ```

### Configuration

The application configuration is located in `src/main/resources/application.properties`. Ensure that the MongoDB connection settings are correct.

### Usage

- Access the application at `http://localhost:8080`.
- Use the form to add new products and categories.
- List, search, and view products and categories.

### Endpoints

- `GET /listar` - List all products
- `GET /form` - Show the form to add a new product
- `POST /form` - Save a new product
- `GET /editar/{id}` - Show the form to edit a product
- `POST /editar/{id}` - Update a product
- `GET /eliminar/{id}` - Delete a product
- `POST /eliminar/{id}` - Confirm deletion of a product
### File Upload

- The form allows uploading an image file for the product.
- The file is validated to ensure it is an image.

### Running Tests

To run the tests, use the following command:
```sh
mvn test