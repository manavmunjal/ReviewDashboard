package com.reviewdashboard.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * A Feign client for communicating with the Company service.
 *
 * <p>This interface defines methods to make HTTP requests to the company-related endpoints
 * available at the specified URL.
 */
@FeignClient(name = "companyClient", url = "${company.client.url}")
public interface CompanyClient {
  /**
   * Retrieves the average rating for a specific company by its ID.
   *
   * @param companyId The unique identifier of the company.
   * @param userId The user ID for authentication.
   * @return A {@link ResponseEntity} containing the average rating as a {@link Double}.
   */
  @GetMapping("/{companyId}/average-rating")
  ResponseEntity<Double> getAverageRating(
      @PathVariable("companyId") String companyId,
      @RequestHeader("X-User-Id") String userId);
}
