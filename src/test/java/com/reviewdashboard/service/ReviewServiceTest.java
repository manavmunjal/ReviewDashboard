package com.reviewdashboard.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.reviewdashboard.client.ProductClient;
import com.reviewdashboard.model.ReviewDto;
import com.reviewdashboard.model.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

/**
 * Unit tests for the {@link ReviewService} class.
 *
 * <p>This test class covers all equivalence partitions for {@link ReviewService} methods:
 *
 * <ul>
 *   <li>addReview(String, ReviewDto)
 *       <ul>
 *         <li>Valid product ID and review → returns created ReviewDto
 *         <li>Invalid product ID → throws IllegalArgumentException
 *         <li>Invalid review → throws IllegalArgumentException
 *         <li>Unexpected client error → throws RuntimeException
 *       </ul>
 *   <li>getAverageRating(String)
 *       <ul>
 *         <li>Valid product ID with ratings → returns ResponseEntity with rating
 *         <li>Valid product ID with no ratings → returns ResponseEntity with null
 *         <li>Invalid product ID → throws IllegalArgumentException
 *         <li>Unexpected client error → throws RuntimeException
 *       </ul>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

  @Mock private ProductClient productClient;

  @InjectMocks private ReviewService reviewService;

  private ReviewDto review;

  /**
   * Initializes common test data before each test method.
   *
   * <p>This method creates a sample {@link ReviewDto} with a comment, rating, and associated user.
   * It ensures that each test has a fresh instance of the review object to avoid shared state
   * between tests.
   */
  @BeforeEach
  public void setUp() {
    review = new ReviewDto();
    review.setComment("Great product!");
    review.setRating(5);
    UserDto user = new UserDto();
    user.setUsername("testuser");
    review.setUser(user);
  }

  // ---------------- addReview tests ----------------

  /**
   * Scenario: Valid product ID and valid review.
   *
   * <p>Expects: addReview returns the created ReviewDto as returned by the client.
   */
  @Test
  public void testAddReview_Valid() {
    when(productClient.postReview(anyString(), any(ReviewDto.class))).thenReturn(review);

    ReviewDto result = reviewService.addReview("123", review);

    assertEquals(review, result);
  }

  /**
   * Scenario: Invalid product ID.
   *
   * <p>Expects: addReview throws IllegalArgumentException from the client.
   */
  @Test
  public void testAddReview_InvalidProductId() {
    when(productClient.postReview(anyString(), any(ReviewDto.class)))
        .thenThrow(new IllegalArgumentException("Invalid product ID"));

    assertThrows(IllegalArgumentException.class, () -> reviewService.addReview("invalid", review));
  }

  /**
   * Scenario: Invalid review (missing required fields such as comment or rating).
   *
   * <p>Expects: addReview throws IllegalArgumentException from the client.
   */
  @Test
  public void testAddReview_InvalidReview() {
    ReviewDto invalidReview = new ReviewDto(); // missing comment and rating
    when(productClient.postReview(anyString(), any(ReviewDto.class)))
        .thenThrow(new IllegalArgumentException("Invalid review"));

    assertThrows(
        IllegalArgumentException.class, () -> reviewService.addReview("123", invalidReview));
  }

  /**
   * Scenario: Unexpected error occurs in the client while adding review.
   *
   * <p>Expects: addReview throws RuntimeException.
   */
  @Test
  public void testAddReview_UnexpectedError() {
    when(productClient.postReview(anyString(), any(ReviewDto.class)))
        .thenThrow(new RuntimeException("Service unavailable"));

    assertThrows(RuntimeException.class, () -> reviewService.addReview("123", review));
  }

  // ---------------- getAverageRating tests ----------------

  /**
   * Scenario: Valid product ID with existing ratings.
   *
   * <p>Expects: getAverageRating returns a ResponseEntity containing the expected rating.
   */
  @Test
  public void testGetAverageRating_ValidWithRatings() {
    ResponseEntity<Double> response = ResponseEntity.ok(4.5);
    when(productClient.getAverageRating(anyString())).thenReturn(response);

    ResponseEntity<Double> result = reviewService.getAverageRating("456");

    assertEquals(response, result);
    assertEquals(4.5, result.getBody());
  }

  /**
   * Scenario: Valid product ID but no ratings exist.
   *
   * <p>Expects: getAverageRating returns a ResponseEntity with null body.
   */
  @Test
  public void testGetAverageRating_ValidNoRatings() {
    ResponseEntity<Double> response = ResponseEntity.ok(null);
    when(productClient.getAverageRating(anyString())).thenReturn(response);

    ResponseEntity<Double> result = reviewService.getAverageRating("456");

    assertEquals(null, result.getBody());
  }

  /**
   * Scenario: Invalid product ID.
   *
   * <p>Expects: getAverageRating throws IllegalArgumentException from the client.
   */
  @Test
  public void testGetAverageRating_InvalidProductId() {
    when(productClient.getAverageRating(anyString()))
        .thenThrow(new IllegalArgumentException("Invalid product ID"));

    assertThrows(IllegalArgumentException.class, () -> reviewService.getAverageRating("invalid"));
  }

  /**
   * Scenario: Unexpected error occurs in the client while fetching average rating.
   *
   * <p>Expects: getAverageRating throws RuntimeException.
   */
  @Test
  public void testGetAverageRating_UnexpectedError() {
    when(productClient.getAverageRating(anyString()))
        .thenThrow(new RuntimeException("Service unavailable"));

    assertThrows(RuntimeException.class, () -> reviewService.getAverageRating("123"));
  }
}
