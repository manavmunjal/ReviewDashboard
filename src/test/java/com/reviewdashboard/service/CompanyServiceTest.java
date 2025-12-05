package com.reviewdashboard.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.reviewdashboard.client.CompanyClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

/**
 * Unit tests for the {@link CompanyService} class.
 *
 * <p>This test class covers all equivalence partitions for {@link
 * CompanyService#getAverageRating(String)}:
 *
 * <ul>
 *   Valid company ID with ratings → returns 200 OK with a number
 *   <li>Valid company ID with no ratings → returns 200 OK with null
 *   <li>Invalid company ID → throws {@link IllegalArgumentException}
 *   <li>Unexpected error → throws {@link RuntimeException} or other exceptions
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
public class CompanyServiceTest {

  /** Mocks the {@link CompanyClient} to isolate the service during testing. */
  @Mock private CompanyClient companyClient;

  /** Injects the mocked {@link CompanyClient} into the {@link CompanyService}. */
  @InjectMocks private CompanyService companyService;

  /**
   * Tests the scenario: valid company ID with ratings.
   *
   * <p>Expects a 200 OK response with the expected numeric rating.
   */
  @Test
  public void testGetCompanyAverageRating_ValidWithRatings() {
    String companyId = "123";
    String userId = "user123";
    Double expectedRating = 4.5;

    when(companyClient.getAverageRating(anyString(), anyString()))
        .thenReturn(ResponseEntity.ok(expectedRating));

    ResponseEntity<Double> actual = companyService.getAverageRating(companyId, userId);

    assertEquals(expectedRating, actual.getBody());
  }

  /**
   * Tests the scenario: valid company ID with no ratings.
   *
   * <p>Expects a 200 OK response with a null body.
   */
  @Test
  public void testGetCompanyAverageRating_ValidWithNoRatings() {
    String companyId = "123";
    String userId = "user123";

    when(companyClient.getAverageRating(anyString(), anyString()))
        .thenReturn(ResponseEntity.ok(null));

    ResponseEntity<Double> actual = companyService.getAverageRating(companyId, userId);

    assertEquals(null, actual.getBody());
  }

  /**
   * Tests the scenario: invalid company ID.
   *
   * <p>Expects an {@link IllegalArgumentException} to be thrown.
   */
  @Test
  public void testGetCompanyAverageRating_InvalidCompanyId() {
    String companyId = "invalid";
    String userId = "user123";

    when(companyClient.getAverageRating(anyString(), anyString()))
        .thenThrow(new IllegalArgumentException("Invalid company ID"));

    assertThrows(
        IllegalArgumentException.class,
        () -> companyService.getAverageRating(companyId, userId),
        "Expected IllegalArgumentException for invalid company ID");
  }

  /**
   * Tests the scenario: unexpected error occurs in the client.
   *
   * <p>Expects a {@link RuntimeException} to be thrown.
   */
  @Test
  public void testGetCompanyAverageRating_UnexpectedError() {
    String companyId = "123";
    String userId = "user123";

    when(companyClient.getAverageRating(anyString(), anyString()))
        .thenThrow(new RuntimeException("Service unavailable"));

    assertThrows(
        RuntimeException.class,
        () -> companyService.getAverageRating(companyId, userId),
        "Expected RuntimeException for unexpected client error");
  }
}
