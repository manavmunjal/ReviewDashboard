package com.reviewdashboard.controller;

import com.reviewdashboard.model.ReviewDto;
import com.reviewdashboard.service.CompanyService;
import com.reviewdashboard.service.ReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling product and company reviews.
 *
 * <p>Each API logs request entry, successful response, and errors.
 */
@RestController
@RequestMapping("review")
public class ReviewClientController {

  private static final Logger logger = LoggerFactory.getLogger(ReviewClientController.class);

  private final ReviewService reviewService;
  private final CompanyService companyService;

  public ReviewClientController(ReviewService reviewService, CompanyService companyService) {
    this.reviewService = reviewService;
    this.companyService = companyService;
  }

  /**
   * Adds a review for a product.
   *
   * @param productId The product ID.
   * @param review The review DTO.
   * @param userId The user ID for authentication.
   * @return ResponseEntity with status and body.
   */
  @PostMapping("product/{productId}")
  public ResponseEntity<?> addReview(
      @PathVariable String productId,
      @RequestBody ReviewDto review,
      @RequestHeader("X-User-Id") String userId) {

    if (logger.isInfoEnabled()) {
      logger.info("Received request to add review for productId={}", productId);
    }

    try {
      ReviewDto createdReview = reviewService.addReview(productId, review, userId);

      if (logger.isInfoEnabled()) {
        logger.info("Successfully added review for productId={}", productId);
      }

      return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);

    } catch (IllegalArgumentException e) {
      if (logger.isWarnEnabled()) {
        logger.warn("Bad request for productId={}: {}", productId, e.getMessage());
      }
      return ResponseEntity.badRequest().body(e.getMessage());

    } catch (Exception e) {
      if (logger.isErrorEnabled()) {
        logger.error("Error adding review for productId={}", productId, e);
      }
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Failed to create review: " + e.getMessage());
    }
  }

  /**
   * Retrieves the average rating for a product.
   *
   * @param productId The product ID.
   * @param userId The user ID for authentication.
   * @return ResponseEntity with status and average rating.
   */
  @GetMapping("product/{productId}/average-rating")
  public ResponseEntity<?> getProductAverageRating(
      @PathVariable String productId,
      @RequestHeader("X-User-Id") String userId) {

    if (logger.isInfoEnabled()) {
      logger.info("Received request to fetch average rating for productId={}", productId);
    }

    try {
      ResponseEntity<Double> response = reviewService.getAverageRating(productId, userId);

      if (response.getBody() == null) {
        if (logger.isWarnEnabled()) {
          logger.warn("No reviews found for productId={}", productId);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body("No reviews found for productId: " + productId);
      }

      if (logger.isInfoEnabled()) {
        logger.info(
            "Successfully fetched average rating for productId={} : {}",
            productId,
            response.getBody());
      }
      return ResponseEntity.ok(response.getBody());

    } catch (IllegalArgumentException e) {
      if (logger.isWarnEnabled()) {
        logger.warn("Bad request for productId={}: {}", productId, e.getMessage());
      }
      return ResponseEntity.badRequest().body(e.getMessage());

    } catch (Exception e) {
      if (logger.isErrorEnabled()) {
        logger.error("Error fetching product rating for productId={}", productId, e);
      }
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Failed to fetch product rating: " + e.getMessage());
    }
  }

  /**
   * Retrieves the average rating for a company.
   *
   * @param companyId The company ID.
   * @param userId The user ID for authentication.
   * @return ResponseEntity with status and average rating.
   */
  @GetMapping("company/{companyId}/average-rating")
  public ResponseEntity<?> getCompanyAverageRating(
      @PathVariable String companyId,
      @RequestHeader("X-User-Id") String userId) {

    if (logger.isInfoEnabled()) {
      logger.info("Received request to fetch average rating for companyId={}", companyId);
    }

    try {
      ResponseEntity<Double> response = companyService.getAverageRating(companyId, userId);

      if (response.getBody() == null) {
        if (logger.isWarnEnabled()) {
          logger.warn("No reviews found for companyId={}", companyId);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body("No reviews found for companyId: " + companyId);
      }

      if (logger.isInfoEnabled()) {
        logger.info(
            "Successfully fetched average rating for companyId={} : {}",
            companyId,
            response.getBody());
      }
      return ResponseEntity.ok(response.getBody());

    } catch (IllegalArgumentException e) {
      if (logger.isWarnEnabled()) {
        logger.warn("Bad request for companyId={}: {}", companyId, e.getMessage());
      }
      return ResponseEntity.badRequest().body(e.getMessage());

    } catch (Exception e) {
      if (logger.isErrorEnabled()) {
        logger.error("Error fetching company rating for companyId={}", companyId, e);
      }
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Failed to fetch company rating: " + e.getMessage());
    }
  }
}
