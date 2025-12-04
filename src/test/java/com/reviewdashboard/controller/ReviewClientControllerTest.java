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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Unit tests for the {@link ReviewClientController} class.
 *
 * <p>This test class verifies the functionality of the controller, ensuring that it correctly
 * delegates calls to the underlying {@link ReviewService} and {@link CompanyService}, and handles
 * HTTP status codes and error scenarios properly.
 *
 * <p>The tests cover successful execution, validation errors (bad requests), not found scenarios,
 * and unexpected internal server errors.
 */
@ExtendWith(MockitoExtension.class)
public class ReviewClientControllerTest {

  /** Mocked instance of {@link ReviewService} to isolate the controller during tests. */
  @Mock private ReviewService reviewService;

  /** Mocked instance of {@link CompanyService} to isolate the controller during tests. */
  @Mock private CompanyService companyService;

  /** The {@link ReviewClientController} instance under test. */
  private ReviewClientController controller;

  /**
   * Sets up the test environment before each test method.
   *
   * <p>This method initializes the mock services using Mockito and injects them into a new instance
   * of {@link ReviewClientController}.
   */
  @BeforeEach
  public void setUp() {
    controller = new ReviewClientController(reviewService, companyService);
  }

  // ============================================================
  // addReview() tests
  // ============================================================

  /**
   * Tests that {@link ReviewClientController#addReview(String, ReviewDto, String)} returns a 201
   * CREATED response when the review is successfully added.
   */
  @Test
  public void testAddReview_Success() {
    final String productId = "123";
    final String userId = "user123";
    ReviewDto review = new ReviewDto();
    review.setComment("Awesome!");
    review.setRating(5);
    review.setUser(new UserDto());

    when(reviewService.addReview(anyString(), any(ReviewDto.class), anyString()))
        .thenReturn(review);

    ResponseEntity<?> response = controller.addReview(productId, review, userId);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(review, response.getBody());
  }

  /**
   * Tests that {@link ReviewClientController#addReview(String, ReviewDto, String)} returns a 400
   * BAD REQUEST when the service throws an {@link IllegalArgumentException}.
   */
  @Test
  public void testAddReview_BadRequest() {
    when(reviewService.addReview(anyString(), any(ReviewDto.class), anyString()))
        .thenThrow(new IllegalArgumentException("Invalid product"));

    ResponseEntity<?> response = controller.addReview("123", new ReviewDto(), "user123");

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Invalid product", response.getBody());
  }

  /**
   * Tests that {@link ReviewClientController#addReview(String, ReviewDto, String)} returns a 500
   * INTERNAL SERVER ERROR when the service throws an unexpected exception.
   */
  @Test
  public void testAddReview_InternalError() {
    when(reviewService.addReview(anyString(), any(ReviewDto.class), anyString()))
        .thenThrow(new RuntimeException("DB failure"));

    ResponseEntity<?> response = controller.addReview("123", new ReviewDto(), "user123");

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("Failed to create review: DB failure", response.getBody());
  }

  // ============================================================
  // getProductAverageRating() tests
  // ============================================================

  /**
   * Tests that {@link ReviewClientController#getProductAverageRating(String, String)} returns a 200
   * OK with the expected rating when reviews exist for the product.
   */
  @Test
  public void testGetProductAverageRating_Success() {
    when(reviewService.getAverageRating("123", "user123")).thenReturn(ResponseEntity.ok(4.5));

    ResponseEntity<?> response = controller.getProductAverageRating("123", "user123");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(4.5, response.getBody());
  }

  /**
   * Tests that {@link ReviewClientController#getProductAverageRating(String, String)} returns a 404
   * NOT FOUND when no reviews exist for the product.
   */
  @Test
  public void testGetProductAverageRating_NotFound() {
    when(reviewService.getAverageRating("123", "user123")).thenReturn(ResponseEntity.ok(null));

    ResponseEntity<?> response = controller.getProductAverageRating("123", "user123");

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("No reviews found for productId: 123", response.getBody());
  }

  /**
   * Tests that {@link ReviewClientController#getProductAverageRating(String, String)} returns a 400
   * BAD REQUEST when the service throws an {@link IllegalArgumentException}.
   */
  @Test
  public void testGetProductAverageRating_BadRequest() {
    when(reviewService.getAverageRating("123", "user123"))
        .thenThrow(new IllegalArgumentException("Invalid product id"));

    ResponseEntity<?> response = controller.getProductAverageRating("123", "user123");

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Invalid product id", response.getBody());
  }

  /**
   * Tests that {@link ReviewClientController#getProductAverageRating(String, String)} returns a 500
   * INTERNAL SERVER ERROR when the service throws an unexpected exception.
   */
  @Test
  public void testGetProductAverageRating_InternalError() {
    when(reviewService.getAverageRating("123", "user123"))
        .thenThrow(new RuntimeException("Timeout"));

    ResponseEntity<?> response = controller.getProductAverageRating("123", "user123");

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("Failed to fetch product rating: Timeout", response.getBody());
  }

  // ============================================================
  // getCompanyAverageRating() tests
  // ============================================================

  /**
   * Tests that {@link ReviewClientController#getCompanyAverageRating(String, String)} returns a 200
   * OK with the expected rating when reviews exist for the company.
   */
  @Test
  public void testGetCompanyAverageRating_Success() {
    when(companyService.getAverageRating("456", "user123")).thenReturn(ResponseEntity.ok(4.2));

    ResponseEntity<?> response = controller.getCompanyAverageRating("456", "user123");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(4.2, response.getBody());
  }

  /**
   * Tests that {@link ReviewClientController#getCompanyAverageRating(String, String)} returns a 404
   * NOT FOUND when no reviews exist for the company.
   */
  @Test
  public void testGetCompanyAverageRating_NotFound() {
    when(companyService.getAverageRating("456", "user123")).thenReturn(ResponseEntity.ok(null));

    ResponseEntity<?> response = controller.getCompanyAverageRating("456", "user123");

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("No reviews found for companyId: 456", response.getBody());
  }

  /**
   * Tests that {@link ReviewClientController#getCompanyAverageRating(String, String)} returns a 400
   * BAD REQUEST when the service throws an {@link IllegalArgumentException}.
   */
  @Test
  public void testGetCompanyAverageRating_BadRequest() {
    when(companyService.getAverageRating("456", "user123"))
        .thenThrow(new IllegalArgumentException("Invalid company id"));

    ResponseEntity<?> response = controller.getCompanyAverageRating("456", "user123");

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Invalid company id", response.getBody());
  }

  /**
   * Tests that {@link ReviewClientController#getCompanyAverageRating(String, String)} returns a 500
   * INTERNAL SERVER ERROR when the service throws an unexpected exception.
   */
  @Test
  public void testGetCompanyAverageRating_InternalError() {
    when(companyService.getAverageRating("456", "user123"))
        .thenThrow(new RuntimeException("Service unavailable"));

    ResponseEntity<?> response = controller.getCompanyAverageRating("456", "user123");

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("Failed to fetch company rating: Service unavailable", response.getBody());
  }
}
