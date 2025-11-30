package com.reviewdashboard.service;

import com.reviewdashboard.client.CompanyClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Service layer for handling company-related business logic.
 *
 * <p>This service acts as an intermediary between the controller and the {@link CompanyClient},
 * delegating calls to the external company microservice.
 */
@Service
public class CompanyService {
  private final CompanyClient companyClient;

  /**
   * Constructs a new CompanyService with the necessary Feign client.
   *
   * @param companyClient The Feign client for communicating with the company service.
   */
  public CompanyService(CompanyClient companyClient) {
    this.companyClient = companyClient;
  }

  /**
   * Retrieves the average rating for a specific company by delegating the call to the
   * CompanyClient.
   *
   * @param companyId The unique identifier of the company.
   * @return A {@link ResponseEntity} containing the average rating as a {@link Double}.
   */
  public ResponseEntity<Double> getAverageRating(String companyId) {
    return companyClient.getAverageRating(companyId);
  }
}
