package com.reviewdashboard.model;

/**
 * Data Transfer Object for representing a review.
 * <p>
 * This class is used to transfer review data between different layers of the application,
 * such as between the controller and service layers, or between microservices.
 * </p>
 */
public class ReviewDTO {
  /**
   * The unique identifier of the review.
   */
  private String id;
  /**
   * The textual content of the review.
   */
  private String comment;
  /**
   * The numerical rating given in the review (e.g., 1 to 5).
   */
  private double rating;
  /**
   * The user who submitted the review.
   */
  private UserDTO user;

  /**
   * Gets the unique identifier of the review.
   * @return The review ID.
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the unique identifier of the review.
   * @param id The review ID.
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Gets the comment text of the review.
   * @return The review comment.
   */
  public String getComment() {
    return comment;
  }

  /**
   * Sets the comment text of the review.
   * @param comment The review comment.
   */
  public void setComment(String comment) {
    this.comment = comment;
  }

  /**
   * Gets the numerical rating of the review.
   * @return The rating.
   */
  public double getRating() {
    return rating;
  }

  /**
   * Sets the numerical rating of the review.
   * @param rating The rating.
   */
  public void setRating(double rating) {
    this.rating = rating;
  }

  /**
   * Gets the user who wrote the review.
   * @return The user DTO.
   */
  public UserDTO getUser() {
    return user;
  }

  /**
   * Sets the user who wrote the review.
   * @param user The user DTO.
   */
  public void setUser(UserDTO user) {
    this.user = user;
  }
}
