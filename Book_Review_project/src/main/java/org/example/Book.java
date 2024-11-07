package org.example;
import java.util.ArrayList;
import java.util.List;

public abstract class Book implements Displayable, Comparable<Book> {
    private String title;
    private String author;
    private List<Review> reviews;
    private List<Integer> ratings;

    public Book(String title, String author, int rating) {
        this.title = title;
        this.author = author;
        this.ratings = new ArrayList<>();
    }

    // Add a rating to the list
    public void addRating(int rating) {
        if (rating >= 1 && rating <= 5) { // Assuming a rating range of 1 to 5
            ratings.add(rating);
        } else {
            System.out.println("Invalid rating. Please provide a rating between 1 and 5.");
        }
    }

    // Calculate the average rating
    public double getAverageRating() {
        if (ratings.isEmpty()) {
            return 0.0; // No ratings, so return 0.0
        }

        int sum = 0;
        for (int rating : ratings) {
            sum += rating;
        }

        return (double) sum / ratings.size();
    }

    // Optional: Get the number of ratings for the book
    public int getNumberOfRatings() {
        return ratings.size();
    }

    // Getter methods for title and author
    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    @Override
    public int compareTo(Book other) {
        return Double.compare(this.getAverageRating(), other.getAverageRating());
    }

    public void setRating(int rating) {

    }

    public List<Review> getReviews() {
        return reviews;
    }
}


