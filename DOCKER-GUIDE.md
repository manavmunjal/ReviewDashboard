# Dockerization Guide for the Review Dashboard Application

This document provides a detailed explanation of the `Dockerfile` used to containerize the Review Dashboard application. The Dockerfile uses a multi-stage build process to create a lean, optimized, and secure Docker image.

## Understanding the `Dockerfile`

The `Dockerfile` is structured into two main stages: the **build stage** and the **final stage**. This approach ensures that the final image contains only the necessary runtime dependencies, excluding the build tools like Maven and the JDK, which results in a smaller and more secure image.

---

### Stage 1: The Build Stage

This stage is responsible for compiling the Java source code and packaging it into an executable JAR file.

```dockerfile
# Use a Maven image to build the application
FROM maven:3.8.5-openjdk-17 AS build

# Set the working directory
WORKDIR /app

# Copy the pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the rest of the application code
COPY src ./src

# Build the application
RUN mvn clean install
```

**Explanation of Commands:**

1.  **`FROM maven:3.8.5-openjdk-17 AS build`**
    *   This line initializes the build stage and specifies the base image. We use an official Maven image that comes with both Maven (`3.8.5`) and OpenJDK 17, providing the perfect environment for building our Spring Boot application.
    *   `AS build` names this stage "build," which we will reference later.

2.  **`WORKDIR /app`**
    *   This sets the working directory inside the container to `/app`. All subsequent commands will be executed from this directory.

3.  **`COPY pom.xml .`**
    *   This copies the `pom.xml` file into the container.

4.  **`RUN mvn dependency:go-offline`**
    *   This command downloads all the project dependencies defined in `pom.xml`. This step is intentionally done *before* copying the source code. Thanks to Docker's layer caching, this layer will only be re-executed if the `pom.xml` file changes, which significantly speeds up subsequent builds.

5.  **`COPY src ./src`**
    *   This copies the application's source code (the `src` directory) into the container.

6.  **`RUN mvn clean install`**
    *   This command compiles the source code and packages the application into a JAR file. The resulting JAR will be located in the `/app/target` directory.

---

### Stage 2: The Final Stage

This stage is responsible for creating the final, lightweight image that will be used to run the application.

```dockerfile
# Use a smaller OpenJDK image to run the application
FROM eclipse-temurin:17-jre-focal

# Set the working directory
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/review-dashboard-1.0.0.jar .

# Expose the port the application runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "review-dashboard-1.0.0.jar"]
```

**Explanation of Commands:**

1.  **`FROM eclipse-temurin:17-jre-focal`**
    *   This line initializes the final stage. We use a minimal Java Runtime Environment (JRE) image, `eclipse-temurin:17-jre-focal`, which is much smaller than the full JDK image used in the build stage. This is all we need to run the compiled Java application.

2.  **`WORKDIR /app`**
    *   Again, we set the working directory to `/app`.

3.  **`COPY --from=build /app/target/review-dashboard-1.0.0.jar .`**
    *   This is the key command in a multi-stage build. It copies the JAR file that was created in the `build` stage (`/app/target/review-dashboard-1.0.0.jar`) into the current stage. This allows us to transfer the build artifact without carrying over any of the build dependencies.

4.  **`EXPOSE 8080`**
    *   This command informs Docker that the container will listen on port `8081` at runtime. This does not actually publish the port; it serves as documentation and allows for easier port mapping when running the container.

5.  **`ENTRYPOINT ["java", "-jar", "review-dashboard-1.0.0.jar"]`**
    *   This command specifies the command that will be executed when the container starts. It runs the Spring Boot application using the `java -jar` command.

## How to Build and Run the Docker Image

To build and run the Docker image, follow these steps:

1.  **Build the Image:**
    Open a terminal in the root of the project (where the `Dockerfile` is located) and run the following command:
    ```bash
    docker build -t review-dashboard .
    ```
    *   `docker build`: The command to build a Docker image.
    *   `-t review-dashboard`: Tags the image with the name `review-dashboard`.
    *   `.`: Specifies that the build context is the current directory.

2.  **Run the Container:**
    Once the image is built, you can run it as a container with this command:
    ```bash
    docker run -p 8081:8081 review-dashboard
    ```
    *   `docker run`: The command to run a Docker container.
    *   `-p 8081:8081`: Maps port `8081` on the host machine to port `8081` in the container, allowing you to access the application from your browser at `http://localhost:8081`.
    *   `review-dashboard`: The name of the image to run.
---
