# Backend
A Java Backend for our Codenames Game using Spring Boot 

## Running the Backend with Docker

This project supports running the Spring Boot backend using Docker.

### Prerequisites
- Docker installed and running

### Build the Docker image

```bash
docker build -t backend-local .
```

### Run the container
```docker run -p 8080:8080 backend-local```

### Access the application

The backend will be available at:
http://localhost:8080
