# 🎯 Microservice Tutorials

This repository contains a collection of microservices for a **Quiz Application** built using **Spring Boot**, **Spring Cloud**, and related technologies.

---

## 🧭 How Microservices Work in This Project

In this microservices architecture, each service operates independently but communicates seamlessly through a centralized service discovery mechanism.

### 🔹 Service Discovery and Registration
- **Eureka Server (Service Registry)**: Runs on port **8761** and acts as the central registry. All microservices automatically register themselves with Eureka upon startup, providing their service name, IP address, and port.
- **Dynamic Tracking**: Regardless of the port each service is running on (e.g., Question Service on 8080, Quiz Service on 8090), Eureka tracks and maintains a live registry of all active instances. This allows services to locate each other dynamically without hard-coded URLs.

### 🔹 API Gateway and Routing
- **API Gateway**: Runs on port **8765** and serves as the single entry point for all client requests. It integrates with Eureka to discover available services.
- **Automatic Configuration**: The Gateway uses service discovery to route requests to the appropriate microservice based on the service name, not the port.  
  Example:
http://localhost:8765/question-service/question/allQuestions

markdown
Copy code
is automatically routed to the Question Service instance registered with Eureka.
- **Load Balancing**: If multiple instances of a service are running, the Gateway can distribute requests across them.

### 🔹 Inter-Service Communication
- **Feign Clients**: Services like Quiz Service use OpenFeign (a declarative REST client) to call other services. Feign integrates with Eureka to resolve service names to actual URLs at runtime.
- **Example Flow**:
1. Quiz Service needs questions → Calls Question Service via Feign using the service name `QUESTION-SERVICE`.
2. Eureka resolves `QUESTION-SERVICE` to the current IP/port of the Question Service instance.
3. The call is made dynamically, ensuring communication even if ports change.

---

## 🔄 Dynamic Service Discovery (Port Independence)

This project uses **Netflix Eureka** for service registration and discovery, enabling all microservices to communicate dynamically through their **registry names** rather than hardcoded URLs or ports.

### ⚙️ How It Works
- Each microservice registers itself with the **Eureka Server** on startup, providing its service name, host, and port.
- The **Eureka Server** maintains a live directory of all active services.
- When a service's port or IP changes, Eureka automatically updates its record.
- Other services (like the **API Gateway** or **Feign clients**) locate the service by its **name** and route requests to the updated location automatically.

### 📘 Example
| Service Name | Example Port | Description |
|---------------|--------------|--------------|
| `QUESTION-SERVICE` | 8081 | Handles quiz question logic |
| `QUIZ-SERVICE` | 8090 | Manages quiz creation and scoring |
| `API-GATEWAY` | 8765 | Routes external requests to microservices |

A request like:
http://localhost:8765/question-service/question/allQuestions

markdown
Copy code
is automatically routed through:
- **API Gateway → Eureka → Question Service (current IP & port)**

Even if `QUESTION-SERVICE` restarts on a new port (e.g., 8082),  
Eureka instantly updates its registry, and the Gateway continues routing without any configuration changes.

### ✅ Benefits
- **Port Independence**: Services can restart or scale dynamically on any available port.
- **No Hardcoded URLs**: Communication happens via service names only.
- **Auto-Discovery**: Eureka ensures that all running instances are tracked and discoverable.
- **Simplified Maintenance**: No need to update configurations when ports or IPs change.

---

## ⚖️ Load Balancing and Fault Tolerance

- The **API Gateway** automatically load-balances requests across multiple instances of the same service.
- If multiple `QUESTION-SERVICE` instances are running, Eureka distributes traffic evenly between them.
- **Fault Tolerance**: When an instance goes down, Eureka removes it from the registry after a short delay, preventing failed routing attempts.
- **High Availability**: You can scale horizontally by running more instances of the same service on different ports or machines.

---

## 🧩 Summary of Dynamic Features

| Feature | Description |
|----------|--------------|
| **Dynamic Discovery** | Services register and locate each other automatically via Eureka |
| **Port Flexibility** | Services can run on any port without breaking routing |
| **Service Naming** | Requests use logical service names instead of IPs |
| **Gateway Routing** | API Gateway forwards requests dynamically based on Eureka registry |
| **Automatic Updates** | Eureka updates service records when ports/IPs change |
| **Load Balancing** | Requests are distributed across multiple service instances |
| **Fault Handling** | Downed services are removed from the registry automatically |

---

## 🧱 Services Overview

### 1. 🧭 Service Registry (Eureka Server)
- **Port**: 8761  
- **Purpose**: Acts as the service discovery server for registering and discovering microservices.  
- **Technologies**: Spring Cloud Netflix Eureka Server  
- **Configuration**:
  - Application Name: `service-registry`
  - Does not register itself (server mode)

### 2. 🚪 API Gateway
- **Port**: 8765  
- **Purpose**: Provides a single entry point for all client requests and routes them to appropriate microservices.  
- **Technologies**: Spring Cloud Gateway, Eureka Client  
- **Features**:
  - Service discovery routing enabled  
  - Lowercase service ID routing  
  - Local Eureka client configuration  

### 3. ❓ Question Service
- **Port**: 8080  
- **Purpose**: Manages quiz questions, including CRUD operations and question retrieval.  
- **Technologies**: Spring Boot, JPA, PostgreSQL, Eureka Client  
- **Database**: PostgreSQL (`questiondb`)  
- **Endpoints**:
  - `GET /question/allQuestions`
  - `GET /question/category/{category}`
  - `POST /question/add`
  - `GET /question/generate`
  - `POST /question/getQuestions`
  - `POST /question/getScore`

### 4. 🧠 Quiz Service
- **Port**: 8090  
- **Purpose**: Manages quiz creation, retrieval, and submission.  
- **Technologies**: Spring Boot, JPA, PostgreSQL, Eureka Client, OpenFeign  
- **Database**: PostgreSQL (`quizdb`)  
- **Endpoints**:
  - `POST /quiz/create`
  - `GET /quiz/get/{id}`
  - `POST /quiz/submit/{id}`  
- **Communication**: Uses Feign client to connect with `QUESTION-SERVICE`.

---

## 🧰 Technologies Used

- **Java**: 17  
- **Spring Boot**: 3.1.x  
- **Spring Cloud**: 2022.0.3  
- **Database**: PostgreSQL  
- **ORM**: Hibernate (JPA)  
- **Service Discovery**: Netflix Eureka  
- **API Gateway**: Spring Cloud Gateway  
- **Inter-service Communication**: OpenFeign  
- **Build Tool**: Maven  

---

## 🗄️ Database Setup

Two PostgreSQL databases are required:
1. `questiondb` for **Question Service**  
2. `quizdb` for **Quiz Service**

### Sample SQL Data

```sql
CREATE TABLE question (
    id SERIAL PRIMARY KEY,
    question_title VARCHAR(255),
    option1 VARCHAR(255),
    option2 VARCHAR(255),
    option3 VARCHAR(255),
    option4 VARCHAR(255),
    right_answer VARCHAR(255),
    difficultylevel VARCHAR(50),
    category VARCHAR(50)
);

INSERT INTO question (id, category, difficultylevel, option1, option2, option3, option4, question_title, right_answer) VALUES
(1, 'JAVA', 'Easy', 'class', 'interface', 'extends', 'implements', 'Which Java keyword is used to create a subclass?', 'extends'),
(2, 'Java', 'Easy', '4', '5', '6', 'Compile error', 'What is the output of the following Java code snippet?', '5'),
(3, 'Java', 'Easy', 'true', 'false', '0', 'null', 'In Java, what is the default value of an uninitialized boolean variable?', 'false'),
(4, 'Java', 'Easy', 'try', 'throw', 'catch', 'finally', 'Which Java keyword is used to explicitly throw an exception?', 'throw'),
(5, 'Java', 'Easy', 'It indicates that a variable is constant.', 'It indicates that a method can be accessed without creating an instance of the class.', 'It indicates that a class cannot be extended.', 'It indicates that a variable is of primitive type.', 'What does the "static" keyword mean in Java?', 'It indicates that a method can be accessed without creating an instance of the class.'),
(6, 'Java', 'Easy', 'constant int x = 5;', 'final int x = 5;', 'static int x = 5;', 'readonly int x = 5;', 'What is the correct way to declare a constant variable in Java?', 'final int x = 5;'),
(7, 'Java', 'Easy', 'for loop', 'while loop', 'do-while loop', 'switch loop', 'Which loop in Java allows the code to be executed at least once?', 'do-while loop'),
(8, 'Java', 'Easy', 'To terminate a loop or switch statement and transfer control to the next statement.', 'To skip the rest of the code in a loop and move to the next iteration.', 'To define a label for a loop or switch statement.', 'To check a condition and execute a block of code repeatedly.', 'What is the purpose of the "break" statement in Java?', 'To terminate a loop or switch statement and transfer control to the next statement.'),
(9, 'Java', 'Easy', '+', '-', '*', '/', 'Which Java operator is used to concatenate two strings?', '+'),
(10, 'Java', 'Easy', 'HashMap', 'ArrayList', 'LinkedList', 'HashSet', 'In Java, which collection class provides an implementation of a dynamic array?', 'ArrayList'),
(11, 'Python', 'Easy', 'count()', 'size()', 'length()', 'len()', 'Which Python function is used to calculate the length of a list?', 'len()'),
(12, 'Python', 'Easy', '[1, 2, 3]', '[1, 2, 3, 4]', '[4, 3, 2, 1]', 'Error', 'What is the output of the following Python code snippet?', '[1, 2, 3, 4]'),
(13, 'Python', 'Easy', 'break', 'continue', 'pass', 'return', 'Which Python statement is used to exit from a loop prematurely?', 'break'),
(14, 'Python', 'Easy', 'To generate a random number within a given range.', 'To iterate over a sequence of numbers.', 'To sort a list in ascending order.', 'To calculate the length of a string.', 'What is the purpose of the "range()" function in Python?', 'To iterate over a sequence of numbers.'),
(15, 'Python', 'Easy', 'int', 'float', 'str', 'list', 'In Python, which data type is mutable?', 'list'),
(16, 'Python', 'Easy', 'datetime', 'math', 'os', 'sys', 'Which Python module is used for working with dates and times?', 'datetime');
```

## 🚀 Running the Application

### 🧩 Startup Order
1. Start the **Service Registry**  
   - URL: `http://localhost:8761`
2. Start the **API Gateway**  
   - URL: `http://localhost:8765`
3. Start the **Question Service**
4. Start the **Quiz Service**

All services will register automatically with Eureka and become visible in the dashboard.

### 🔗 Access Through API Gateway
| Service | Example Endpoint |
|---------|------------------|
| Question Service | `http://localhost:8765/question-service/question/allQuestions` |
| Quiz Service | `http://localhost:8765/quiz-service/quiz/get/1` |

---

## ⚙️ Configuration Overview

Each microservice contains its own `application.properties` for:

- Application name (`spring.application.name`)
- Server port (`server.port`)
- Database configuration
- Eureka client setup (`eureka.client.service-url.defaultZone`)

### Example (Question Service)
```properties
spring.application.name=question-service
server.port=8081
spring.datasource.url=jdbc:postgresql://localhost:5432/questiondb
spring.datasource.username=postgres
spring.datasource.password=yourpassword
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
```

---

## 💡 Development Notes

- Uses **Lombok** to reduce boilerplate code.
- Implements **Spring Data JPA** for ORM and repository layer.
- Follows **RESTful API** design principles.
- Each microservice is independent, with its own build, run, and database configuration.
- Gateway handles routing, filtering, and global exception handling.
- Future-ready for **Docker**, **Kubernetes**, or **AWS ECS/EKS** deployment.

---

## 🧠 Architecture Diagram

```
             ┌────────────────────┐
             │   Frontend Client  │
             └─────────┬──────────┘
                       │
                       ▼
              ┌────────────────────┐
              │    API Gateway     │  (Port 8765)
              └─────────┬──────────┘
                        │ (Service Discovery)
                        ▼
              ┌────────────────────┐
              │   Eureka Server    │  (Port 8761)
              └─────────┬──────────┘
          ┌──────────────┼──────────────┐
          ▼              ▼              ▼
 ┌──────────────┐ ┌──────────────┐ ┌──────────────┐
 │Question Svc  │ │ Quiz Svc     │ │ Future Svc   │
 │(Port 8081)   │ │ (Port 8090)  │ │ (Optional)   │
 └──────────────┘ └──────────────┘ └──────────────┘
```

---

## ✨ Key Advantages of This Microservice Setup

| Feature | Description |
|----------|--------------|
| **Independence** | Each service runs, deploys, and scales separately. |
| **Dynamic Discovery** | No hardcoded IPs or ports; Eureka manages all instances. |
| **Central Routing** | API Gateway handles all client communication. |
| **Scalability** | Add instances dynamically; load-balancing is automatic. |
| **Fault Tolerance** | Failed services are removed from registry automatically. |
| **Ease of Maintenance** | Update or redeploy one service without affecting others. |
| **Cloud Ready** | Easily deployable with Docker and Kubernetes. |

---

## 🏁 Conclusion

This project demonstrates a complete, scalable, and dynamic microservices ecosystem using:

- **Spring Boot**
- **Spring Cloud Gateway**
- **Netflix Eureka**
- **Feign Clients**
- **PostgreSQL**

It's a production-ready architecture that emphasizes loose coupling, port independence, and dynamic service discovery, providing a strong foundation for real-world distributed systems.

**Author**: ✨ Microservice Tutorial Project by Satyam Singh  
**Tech Stack**: Java · Spring Boot · Spring Cloud · Eureka · PostgreSQL · Maven
