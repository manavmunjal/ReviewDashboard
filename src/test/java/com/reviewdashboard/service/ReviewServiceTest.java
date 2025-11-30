package com.reviewdashboard.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.reviewdashboard.client.ReviewClient;
import com.reviewdashboard.model.ReviewDto;
import com.reviewdashboard.model.UserDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

/** Unit tests for the {@link ReviewService} class. */
@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

  // Mocks the ReviewClient to isolate the service for testing
  @Mock private ReviewClient reviewClient;

  // Injects the mocked ReviewClient into the ReviewService
  @InjectMocks private ReviewService reviewService;

  /** Tests the addReview method. */
  @Test
  public void testAddReview() {
    // Given: Setup the test data
    final String productId = "123";
    final ReviewDto review = new ReviewDto();
    review.setComment("Great product!");
    review.setRating(5);
    final UserDto user = new UserDto();
    user.setUsername("testuser");
    review.setUser(user);

    // Mock the reviewClient to return the review when postReview is called
    when(reviewClient.postReview(anyString(), any(ReviewDto.class))).thenReturn(review);

    // When: Call the method under test
    final ReviewDto result = reviewService.addReview(productId, review);

    // Then: Verify the result
    assertEquals(review, result, "The returned review should match the one from the client.");
  }

  /** Tests the getAverageRating method. */
  @Test
  public void testGetAverageRating() {
    // Given: Setup the test data
    final String productId = "456";
    final Double expectedRating = 4.5;
    final ResponseEntity<Double> response = ResponseEntity.ok(expectedRating);

    // Mock the reviewClient to return the response entity
    when(reviewClient.getAverageRating(anyString())).thenReturn(response);

    // When: Call the method under test
    final ResponseEntity<Double> result = reviewService.getAverageRating(productId);

    // Then: Verify the result
    assertEquals(response, result, "The response entity should match the one from the client.");
    assertEquals(
        expectedRating,
        result.getBody(),
        "The rating in the body should match the expected rating.");
  }
}
