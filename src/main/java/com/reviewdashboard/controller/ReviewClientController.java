package com.reviewdashboard.controller;

import com.reviewdashboard.model.ReviewDto;
import com.reviewdashboard.service.CompanyService;
import com.reviewdashboard.service.ReviewService;
import feign.FeignException;
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
   * <p><b>Equivalence Partitions (EPs)</b>
   *
   * <p><b>Valid EPs:</b> - EP1: Valid productId AND valid ReviewDto: • rating ∈ [0,5] • all
   * mandatory fields present
   *
   * <p><b>Invalid EPs:</b> - EP2: productId null/empty - EP3: ReviewDto null - EP4: rating < 0 -
   * EP5: rating > 5 - EP6: missing mandatory ReviewDto fields - EP7: productId valid but product
   * not found
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
      @RequestHeader(name = "X-User-Id", required = false) String userId) {

    if (logger.isInfoEnabled()) {
      logger.info("Received request to add review for productId={}", productId);
    }

    if (userId == null || userId.trim().isEmpty()) {
      logger.warn("userId is missing from the request header");
      return ResponseEntity.badRequest().body("Please provide a userID in a header");
    }

    try {
      ReviewDto createdReview = reviewService.addReview(productId, review, userId);

      if (createdReview.getRating() < 0.0 || createdReview.getRating() > 5.0) {
        if (logger.isWarnEnabled()) {
          logger.warn("Invalid rating for productId={}", productId);
        }
        return ResponseEntity.badRequest().body("Invalid rating");
      }

      if (logger.isInfoEnabled()) {
        logger.info("Successfully added review for productId={}", productId);
      }

      return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);

    } catch (IllegalArgumentException e) {
      if (logger.isWarnEnabled()) {
        logger.warn("Bad request for productId={}: {}", productId, e.getMessage());
      }
      return ResponseEntity.badRequest().body(e.getMessage());

    } catch (FeignException e) {
      if (e.status() == 401) {
        if (logger.isWarnEnabled()) {
          logger.warn(
              "Authentication failed for userId={} while adding review for productId={}",
              userId,
              productId);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body("Your user ID does not exist. Please create a new user.");
      }
      if (logger.isErrorEnabled()) {
        logger.error("Feign error adding review for productId={}", productId, e);
      }
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Failed to create review: " + e.getMessage());
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
   * <p><b>Equivalence Partitions (EPs)</b>
   *
   * <p><b>Valid EPs:</b> - EP1: Valid productId with ≥1 review → returns average rating - EP2:
   * Valid productId with 0 reviews → returns 404
   *
   * <p><b>Invalid EPs:</b> - EP3: productId null/empty - EP4: productId valid but product not found
   * - EP5: service/DB exception → returns 500
   *
   * @param productId The product ID.
   * @param userId The user ID for authentication.
   * @return ResponseEntity with status and average rating.
   */
  @GetMapping("product/{productId}/average-rating")
  public ResponseEntity<?> getProductAverageRating(
      @PathVariable String productId,
      @RequestHeader(name = "X-User-Id", required = false) String userId) {

    if (logger.isInfoEnabled()) {
      logger.info("Received request to fetch average rating for productId={}", productId);
    }

    if (userId == null || userId.trim().isEmpty()) {
      logger.warn("userId is missing from the request header");
      return ResponseEntity.badRequest().body("Please provide a userID in a header");
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

    } catch (FeignException e) {
      if (e.status() == 401) {
        if (logger.isWarnEnabled()) {
          logger.warn(
              "Authentication failed for userId={} while fetching average rating for productId={}",
              userId,
              productId);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body("Your user ID does not exist. Please create a new user.");
      }
      if (logger.isErrorEnabled()) {
        logger.error("Feign error fetching product rating for productId={}", productId, e);
      }
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Failed to fetch product rating: " + e.getMessage());
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
   * <p><b>Equivalence Partitions (EPs)</b>
   *
   * <p><b>Valid EPs:</b> - EP1: Valid companyId with ≥0 review → returns average rating - EP2:
   * Valid companyId with 0 reviews → returns 404
   *
   * <p><b>Invalid EPs:</b> - EP3: companyId null/empty - EP4: companyId valid but company not found
   * - EP5: service/DB exception → returns 500
   *
   * @param companyId The company ID.
   * @param userId The user ID for authentication.
   * @return ResponseEntity with status and average rating.
   */
  @GetMapping("company/{companyId}/average-rating")
  public ResponseEntity<?> getCompanyAverageRating(
      @PathVariable String companyId,
      @RequestHeader(name = "X-User-Id", required = false) String userId) {

    if (logger.isInfoEnabled()) {
      logger.info("Received request to fetch average rating for companyId={}", companyId);
    }

    if (userId == null || userId.trim().isEmpty()) {
      logger.warn("userId is missing from the request header");
      return ResponseEntity.badRequest().body("Please provide a userID in a header");
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

    } catch (FeignException e) {
      if (e.status() == 401) {
        if (logger.isWarnEnabled()) {
          logger.warn(
              "Authentication failed for userId={} while fetching average rating for companyId={}",
              userId,
              companyId);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body("Your user ID does not exist. Please create a new user.");
      }
      if (logger.isErrorEnabled()) {
        logger.error("Feign error fetching company rating for companyId={}", companyId, e);
      }
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Failed to fetch company rating: " + e.getMessage());
    } catch (Exception e) {
      if (logger.isErrorEnabled()) {
        logger.error("Error fetching company rating for companyId={}", companyId, e);
      }
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Failed to fetch company rating: " + e.getMessage());
    }
  }
}
