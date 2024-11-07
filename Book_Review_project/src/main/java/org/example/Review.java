package org.example;

public class Review implements Savable {
    private String reviewText;
    private int rating;

    public Review(String reviewText, int rating) {
        this.reviewText = reviewText;
        this.rating = rating;
    }

    @Override
    public void save() {
        System.out.println("Saving review: " + reviewText + " with rating " + rating);
        // Code to save review data
    }

    public String getReviewText() {
        return reviewText;
    }
    public int getRating() {
        return rating;
    }
}
