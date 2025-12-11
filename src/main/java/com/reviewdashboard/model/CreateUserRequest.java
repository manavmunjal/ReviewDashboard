package com.reviewdashboard.model;

/** Request payload for creating a new user in the auth service. */
public class CreateUserRequest {
  /** The requested unique user identifier. */
  private String userId;

  /**
   * Default constructor.
   *
   * <p>This constructor is required for JSON deserialization.
   */
  public CreateUserRequest() {
    // no-ops
  }

  /** Convenience constructor. */
  public CreateUserRequest(String userId) {
    this.userId = userId;
  }

  /** Returns the requested user ID. */
  public String getUserId() {
    return userId;
  }

  /** Sets the requested user ID. */
  public void setUserId(String userId) {
    this.userId = userId;
  }
}
