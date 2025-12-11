package com.reviewdashboard.service;

import com.reviewdashboard.client.AuthClient;
import com.reviewdashboard.model.CreateUserRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/** Service layer for user creation via the auth service. */
@Service
public class AuthService {

  private final AuthClient authClient;

  public AuthService(AuthClient authClient) {
    this.authClient = authClient;
  }

  /**
   * Delegates user creation to the auth service.
   *
   * @param userId The desired user ID.
   * @return The raw response from the auth service.
   */
  public ResponseEntity<Void> createUser(final String userId) {
    return authClient.createUser(new CreateUserRequest(userId));
  }
}
