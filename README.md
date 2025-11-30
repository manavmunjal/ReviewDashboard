# Review Dashboard

This is the official repository for the Review Dashboard project.

## Project Overview

A Spring Boot application designed to manage product reviews and integrate with external services for company and product information. It provides RESTful APIs for adding reviews and retrieving average ratings for products and companies.

## Key Features

### 1. **REST API for Review Management**
- **Add Reviews**: Endpoint to submit new reviews for products.
- **Retrieve Average Ratings**: Endpoints to get average ratings for specific products and companies.

### 2. **Service Integration**
- **Feign Clients**: Utilizes Spring Cloud OpenFeign to communicate with external `Review` and `Company` microservices.
- **Service Layer**: Abstracts external service calls, providing a clean interface for the controller.

### 3. **Spring Boot Application**
- **Rapid Development**: Built with Spring Boot for quick setup and deployment.
- **Dependency Injection**: Manages component dependencies using Spring's IoC container.

### 4. **Robust Testing**
- **Unit Tests**: Comprehensive unit tests for controllers, services, and DTOs using JUnit 5 and Mockito.
- **Test Coverage**: Integrated JaCoCo for measuring code coverage.

## Project Structure

```
ReviewDashboard/
├── src/
│   ├── main/
│   │   ├── java/com/reviewdashboard/
│   │   │   ├── ReviewDashBoardApplication.java # Main Spring Boot entry point
│   │   │   ├── client/                       # Feign clients for external services
│   │   │   │   ├── CompanyClient.java
│   │   │   │   └── ReviewClient.java
│   │   │   ├── controller/                   # REST API controllers
│   │   │   │   └── ReviewClientController.java
│   │   │   ├── model/                        # Data Transfer Objects (DTOs)
│   │   │   │   ├── CompanyDTO.java
│   │   │   │   ├── ReviewDTO.java
│   │   │   │   └── UserDTO.java
│   │   │   └── service/                      # Business logic services
│   │   │       ├── CompanyService.java
│   │   │       └── ReviewService.java
│   │   └── resources/
│   │       └── application.yaml              # Spring Boot configuration
│   └── test/
│       ├── java/com/reviewdashboard/
│       │   ├── ReviewDashBoardApplicationTest.java
│       │   ├── client/
│       │   │   # No direct tests for Feign clients (integration tests usually cover this)
│       │   ├── controller/
│       │   │   └── ReviewClientControllerTest.java
│       │   ├── model/
│       │   │   ├── ReviewDTOTest.java
│       │   │   └── UserDTOTest.java
│       │   └── service/
│       │       ├── CompanyServiceTest.java
│       │       └── ReviewServiceTest.java
│       └── resources/
│           └── mockito-extensions/
│               └── org.mockito.plugins.MockMaker # Mockito inline mock maker config
├── pom.xml                                   # Maven configuration
└── README.md                                 # Project documentation

## Technologies Used

- **Java 17+**: Modern Java development
- **Spring Boot 3.3.2**: Framework for building stand-alone, production-grade Spring applications
- **Spring Cloud OpenFeign**: Declarative REST client for easy service integration
- **JUnit Jupiter 5**: Testing framework
- **Mockito**: Mocking framework for unit tests
- **Maven**: Build automation and dependency management
- **JaCoCo**: Code coverage reporting

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Installation

1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd ReviewDashboard
   ```

2. **Install dependencies**:
   ```bash
   mvn clean install
   ```

3. **Compile the project**:
   ```bash
   mvn clean compile
   ```

### Running the Application

**Option 1: Using Maven Spring Boot plugin**
```bash
mvn spring-boot:run
```

**Option 2: Build JAR and run**
```bash
mvn package
java -jar target/myapp-0.0.1-SNAPSHOT.jar
```

## REST API Endpoints

All endpoints are prefixed with `/api`.

- `POST /api/review/{productId}`
  - **Description**: Submits a new review for a given product.
  - **Request Body**: `ReviewDTO` (JSON)
  - **Returns**: `ReviewDTO`
  - **Example**:
    ```json
    POST http://localhost:8080/api/review/product123
    Content-Type: application/json

    {
      "comment": "This product is amazing!",
      "rating": 5.0,
      "user": {
        "username": "testUser"
      }
    }
    ```

- `GET /api/review/product/{productId}/average-rating`
  - **Description**: Retrieves the average rating for a specific product.
  - **Returns**: `ResponseEntity<Double>`
  - **Example**: `GET http://localhost:8080/api/review/product123/average-rating`

- `GET /api/review/company/{companyId}/average-rating`
  - **Description**: Retrieves the average rating for a specific company.
  - **Returns**: `ResponseEntity<Double>`
  - **Example**: `GET http://localhost:8080/api/review/company456/average-rating`

## Testing Requirements

### Run all tests:
```bash
mvn test
```

### Run with coverage:
```bash
mvn clean test jacoco:report
```
To see the JaCoCo report, open:
`target/site/jacoco/index.html`.

### Unit Tests Coverage:
- `ReviewDashBoardApplicationTest`: Verifies application context loading.
- `ReviewClientControllerTest`: Tests API endpoints and service delegation.
- `CompanyServiceTest`: Tests logic related to company average ratings.
- `ReviewServiceTest`: Tests logic related to adding reviews and product average ratings.
- `UserDTOTest`: Verifies `UserDTO` getters and setters.
- `ReviewDTOTest`: Verifies `ReviewDTO` getters, setters, and ID generation.

## AI Usage

- AI was used to generate initial code snippets, add getters/setters, refactor code, and create comprehensive unit tests.
- AI assisted in debugging dependency conflicts and providing solutions for build issues.
- AI helped in generating Javadoc comments for better code documentation.

## Authors
- Development Team: [Manav Munjal, Sreenivas Karthik Bandi, Song Li and Sindhu Krishnamurthy]

---
