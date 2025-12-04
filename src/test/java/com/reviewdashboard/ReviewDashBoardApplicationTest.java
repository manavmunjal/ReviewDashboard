package com.reviewdashboard;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Integration test to verify that the Spring Boot application context loads successfully.
 *
 * <p>This test provides placeholder values for Feign client URLs to allow the context to load
 * without requiring actual external services.
 */
@SpringBootTest
@TestPropertySource(properties = {
    "company.client.url=http://localhost:8081",
    "product.client.url=http://localhost:8082"
})
class ReviewDashBoardApplicationTest {

  /**
   * Ensures that the Spring application context loads successfully with the test properties.
   */
  @Test
  void contextLoads() {}
}
