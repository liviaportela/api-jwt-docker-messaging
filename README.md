# Challenge: Development of a User API with Spring Boot

### This project is an API that uses JWT authentication and Docker for messaging, built with the Spring Boot framework.

## Requirements
Make sure you have the following tools installed:

- **Java Development Kit (JDK) 21**
- **Apache Maven**
- **Docker**
- **MySQL**
- **MongoDB**

## Installation
Follow the steps below to set up the project in your environment.

1. **Clone the repository**
```bash
git clone https://github.com/liviaportela/api-jwt-docker-messaging.git
```

2. **Run the project with Docker**
Use Docker Compose to upload your application containers:
```bash
docker-compose up -d
```

3. **Testing the API**
- **POST /api/users/register** - Creates a new user.
- **POST /api/users/login** - Log in and return the JWT token.
- **PUT /api/users/update-password** - Updates the user's password, requires authentication.

**Project Structure**
- `msusers`: Microservice of users
- `msnotify`: Microservice of notify
- `src/main/java`: Main source code of the microservices
- `docker-compose.yml`: Configuration file for Docker containers
- `pom.xml`: Maven configuration file
