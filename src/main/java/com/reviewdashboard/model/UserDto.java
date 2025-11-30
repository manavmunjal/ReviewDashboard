package com.reviewdashboard.model;

/**
 * Data Transfer Object for representing a user.
 *
 * <p>This class is used to transfer basic user information, typically associated with another
 * entity like a review.
 */
public class UserDto {
  /** The unique identifier of the user. */
  private String id;

  /** The public username of the user. */
  private String username;

  /**
   * Gets the unique identifier of the user.
   *
   * @return The user ID.
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the unique identifier of the user.
   *
   * @param id The user ID.
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Gets the username of the user.
   *
   * @return The username.
   */
  public String getUsername() {
    return username;
  }

  /**
   * Sets the username of the user.
   *
   * @param username The username.
   */
  public void setUsername(String username) {
    this.username = username;
  }
}
