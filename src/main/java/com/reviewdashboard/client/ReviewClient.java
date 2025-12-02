package com.reviewdashboard.client;

import com.reviewdashboard.model.ReviewDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * A Feign client for communicating with the Product/Review service.
 *
 * <p>This interface defines methods to make HTTP requests to the product and review-related
 * endpoints available at the specified URL.
 */
@FeignClient(
    name = "reviewClient",
    url = "${company.client.url}")
public interface ReviewClient {

  /**
   * Submits a new review for a specific product.
   *
   * @param productId The unique identifier of the product being reviewed.
   * @param review The review data to be posted.
   * @return The created {@link ReviewDto} as confirmed by the service.
   */
  @PostMapping("/{productId}/reviews")
  ReviewDto postReview(@PathVariable("productId") String productId, @RequestBody ReviewDto review);

  /**
   * Retrieves the average rating for a specific product by its ID.
   *
   * @param productId The unique identifier of the product.
   * @return A {@link ResponseEntity} containing the average rating as a {@link Double}.
   */
  @GetMapping("/{productId}/average-rating")
  ResponseEntity<Double> getAverageRating(@PathVariable final String productId);
}
