package com.reviewdashboard.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * A Feign client for communicating with the Company service.
 * <p>
 * This interface defines methods to make HTTP requests to the company-related endpoints
 * available at the specified URL.
 * </p>
 */
@FeignClient(name = "companyClient", url = "http://localhost:8080/api/company")
public interface CompanyClient {
  /**
   * Retrieves the average rating for a specific company by its ID.
   * @param companyId The unique identifier of the company.
   * @return A {@link ResponseEntity} containing the average rating as a {@link Double}.
   */
  @GetMapping("/{companyId}/average-rating")
  ResponseEntity<Double> getAverageRating(@PathVariable("companyId") String companyId);
}
