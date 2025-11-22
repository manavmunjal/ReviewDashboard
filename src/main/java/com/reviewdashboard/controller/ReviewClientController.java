package com.reviewdashboard.controller;

import com.reviewdashboard.model.ReviewDTO;
import com.reviewdashboard.service.CompanyService;
import com.reviewdashboard.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller that acts as a facade for review and company-related operations.
 * <p>
 * This controller exposes endpoints that delegate calls to the {@link ReviewService}
 * and {@link CompanyService}, which in turn communicate with external microservices
 * via Feign clients.
 * </p>
 */
@RestController
@RequestMapping("review")
public class ReviewClientController {
  private final ReviewService reviewService;
  private final CompanyService companyService;

  /**
   * Constructs a new ReviewClientController with the necessary service dependencies.
   *
   * @param reviewService  The service for handling product review operations.
   * @param companyService The service for handling company-related operations.
   */
  public ReviewClientController(ReviewService reviewService, CompanyService companyService) {
    this.reviewService = reviewService;
    this.companyService = companyService;
  }

  /**
   * POST /review/product/{productId} : Submits a new review for a given product.
   *
   * @param productId The ID of the product to review.
   * @param review    The review data transfer object containing review details.
   * @return The created {@link ReviewDTO} as returned by the review service.
   */
  @PostMapping("product/{productId}")
  public ReviewDTO addReview(@PathVariable String productId, @RequestBody ReviewDTO review) {
    return reviewService.addReview(productId, review);
  }

  /**
   * GET /review/product/{productId}/average-rating : Retrieves the average rating for a specific product.
   *
   * @param productId The ID of the product.
   * @return A {@link ResponseEntity} containing the average rating as a {@link Double}.
   */
  @GetMapping("product/{productId}/average-rating")
  public ResponseEntity<Double> getProductAverageRating(@PathVariable String productId) {
    return reviewService.getAverageRating(productId);
  }

  /**
   * GET /review/company/{companyId}/average-rating : Retrieves the average rating for a specific company.
   *
   * @param companyId The ID of the company.
   * @return A {@link ResponseEntity} containing the average rating as a {@link Double}.
   */
  @GetMapping("company/{companyId}/average-rating")
  public ResponseEntity<Double> getCompanyAverageRating(@PathVariable String companyId) {
    return companyService.getAverageRating(companyId);
  }

}
