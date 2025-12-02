package com.reviewdashboard.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.reviewdashboard.model.ReviewDto;
import com.reviewdashboard.model.UserDto;
import com.reviewdashboard.service.CompanyService;
import com.reviewdashboard.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

/**
 * Unit tests for the {@link ReviewClientController} class.
 *
 * <p>This test class verifies the functionality of the controller, ensuring that it correctly
 * delegates calls to its underlying services and returns the expected results.
 */
public class ProductClientControllerTest {

  /** Mocked instance of the {@link ReviewService} to isolate the controller during tests. */
  private ReviewService reviewService;

  /** Mocked instance of the {@link CompanyService} to isolate the controller during tests. */
  private CompanyService companyService;

  /** The instance of the {@link ReviewClientController} to be tested. */
  private ReviewClientController reviewClientController;

  /**
   * Sets up the test environment before each test. This method initializes the mock services and
   * the {@link ReviewClientController} instance.
   */
  @BeforeEach
  public void setUp() {
    // Manually create mocks of the services
    reviewService = Mockito.mock(ReviewService.class);
    companyService = Mockito.mock(CompanyService.class);
    // Manually create an instance of the controller, injecting the mock services
    reviewClientController = new ReviewClientController(reviewService, companyService);
  }

  /**
   * Tests the addReview endpoint to ensure it correctly delegates to the service and returns the
   * expected result.
   */
  @Test
  public void testAddReview() {
    // Given: Setup the test data and mock behavior
    final String productId = "123";
    final ReviewDto review = new ReviewDto();
    review.setComment("Great product!");
    review.setRating(5);
    final UserDto user = new UserDto();
    user.setUsername("testuser");
    review.setUser(user);

    // Mock the reviewService to return the review when addReview is called
    when(reviewService.addReview(anyString(), any(ReviewDto.class))).thenReturn(review);

    // When: Call the method under test
    final ReviewDto result = reviewClientController.addReview(productId, review);

    // Then: Verify the result
    assertEquals(review, result, "The returned review should match the mocked one.");
  }

  /** Tests the getProductAverageRating endpoint. */
  @Test
  public void testGetProductAverageRating() {
    // Given
    final String productId = "123";
    final Double expectedRating = 4.5;
    when(reviewService.getAverageRating(productId)).thenReturn(ResponseEntity.ok(expectedRating));

    // When
    final ResponseEntity<Double> response =
        reviewClientController.getProductAverageRating(productId);

    // Then
    assertEquals(ResponseEntity.ok(expectedRating), response);
  }

  /** Tests the getCompanyAverageRating endpoint. */
  @Test
  public void testGetCompanyAverageRating() {
    // Given
    final String companyId = "456";
    final Double expectedRating = 4.2;
    when(companyService.getAverageRating(companyId)).thenReturn(ResponseEntity.ok(expectedRating));

    // When
    final ResponseEntity<Double> response =
        reviewClientController.getCompanyAverageRating(companyId);

    // Then
    assertEquals(ResponseEntity.ok(expectedRating), response);
  }
}
