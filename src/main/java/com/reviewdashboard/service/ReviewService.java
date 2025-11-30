package com.reviewdashboard.service;

import com.reviewdashboard.client.ReviewClient;
import com.reviewdashboard.model.ReviewDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Service layer for handling product review-related business logic.
 *
 * <p>This service acts as an intermediary between the controller and the {@link ReviewClient},
 * delegating calls to the external product/review microservice.
 */
@Service
public class ReviewService {
  private final ReviewClient reviewClient;

  /**
   * Constructs a new ReviewService with the necessary Feign client.
   *
   * @param reviewClient The Feign client for communicating with the product/review service.
   */
  public ReviewService(ReviewClient reviewClient) {
    this.reviewClient = reviewClient;
  }

  /**
   * Submits a new review for a specific product by delegating the call to the ReviewClient.
   *
   * @param productId The unique identifier of the product.
   * @param review The {@link ReviewDto} object containing the review details.
   * @return The created {@link ReviewDto} as confirmed by the external service.
   */
  public ReviewDto addReview(String productId, ReviewDto review) {
    return reviewClient.postReview(productId, review);
  }

  /**
   * Retrieves the average rating for a specific product by delegating the call to the ReviewClient.
   *
   * @param productId The unique identifier of the product.
   * @return A {@link ResponseEntity} containing the average rating as a {@link Double}.
   */
  public ResponseEntity<Double> getAverageRating(String productId) {
    return reviewClient.getAverageRating(productId);
  }
}
