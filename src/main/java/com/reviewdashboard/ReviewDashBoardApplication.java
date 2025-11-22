package com.reviewdashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * The main entry point for the Review Dashboard Spring Boot application.
 * <p>
 * {@link SpringBootApplication} is a convenience annotation that adds all of the following:
 * <ul>
 *     <li>{@code @Configuration}: Tags the class as a source of bean definitions for the application context.</li>
 *     <li>{@code @EnableAutoConfiguration}: Tells Spring Boot to start adding beans based on classpath settings, other beans, and various property settings.</li>
 *     <li>{@code @ComponentScan}: Tells Spring to look for other components, configurations, and services in the {@code com.reviewdashboard} package, allowing it to find the controllers, services, etc.</li>
 * </ul>
 * {@link EnableFeignClients} scans for interfaces that declare they are declarative REST clients (i.e., Feign clients).
 * </p>
 */
@SpringBootApplication
@EnableFeignClients
public class ReviewDashBoardApplication {
  /**
   * The main method which uses Spring Boot's {@link SpringApplication#run} to launch the application.
   *
   * @param args Command line arguments passed to the application.
   */
  public static void main(String[] args) {
    SpringApplication.run(ReviewDashBoardApplication.class, args);
  }
}