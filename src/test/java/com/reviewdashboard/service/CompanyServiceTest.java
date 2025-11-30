package com.reviewdashboard.service;

import com.reviewdashboard.client.CompanyClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the {@link CompanyService} class.
 */
@ExtendWith(MockitoExtension.class)
public class CompanyServiceTest {

    // Mocks the CompanyClient to isolate the service for testing
    @Mock
    private CompanyClient companyClient;

    // Injects the mocked CompanyClient into the CompanyService
    @InjectMocks
    private CompanyService companyService;

    /**
     * Tests the getCompanyAverageRating method.
     */
    @Test
    public void testGetCompanyAverageRating() {
        // Given: Setup the test data and mock behavior
        String companyId = "123";
        Double expectedRating = 4.5;

        // Mock the companyClient to return a successful response with the expected rating
        when(companyClient.getAverageRating(anyString())).thenReturn(ResponseEntity.ok(expectedRating));

        // When: Call the method under test
        ResponseEntity<Double> actualRating = companyService.getAverageRating(companyId);

        // Then: Verify the result
      Assertions.assertNotNull(actualRating.getBody());
      assertEquals(expectedRating, actualRating.getBody().doubleValue(), "The returned rating should match the expected one.");
    }
}
