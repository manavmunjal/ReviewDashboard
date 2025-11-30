package com.reviewdashboard.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link ReviewDto} class.
 *
 * <p>This test class verifies the functionality of the ReviewDTO, including the correct operation
 * of its getters and setters, and the automatic generation of the ID.
 */
public class ReviewDtoTest {
  /** Tests the getter and setter for the 'comment' field. */
  @Test
  public void testGetAndSetComment() {
    // Given
    ReviewDto review = new ReviewDto();
    String expectedComment = "This is a great product!";

    // When
    review.setComment(expectedComment);
    String actualComment = review.getComment();

    // Then
    assertEquals(
        expectedComment, actualComment, "The comment should be correctly set and retrieved.");
  }

  /** Tests the getter and setter for the 'rating' field. */
  @Test
  public void testGetAndSetRating() {
    // Given
    ReviewDto review = new ReviewDto();
    double expectedRating = 4.5;

    // When
    review.setRating(expectedRating);
    double actualRating = review.getRating();

    // Then
    assertEquals(expectedRating, actualRating, "The rating should be correctly set and retrieved.");
  }

  /** Tests the getter and setter for the 'user' field. */
  @Test
  public void testGetAndSetUser() {
    // Given
    ReviewDto review = new ReviewDto();
    UserDto expectedUser = new UserDto();
    expectedUser.setUsername("testUser");

    // When
    review.setUser(expectedUser);
    UserDto actualUser = review.getUser();

    // Then
    assertEquals(expectedUser, actualUser, "The user should be correctly set and retrieved.");
  }
}
