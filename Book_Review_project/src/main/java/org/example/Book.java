//Book.java
/*package org.example;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Book {
    private String title;
    private String author;
    private List<Review> reviews;
    private List<Integer> ratings;
    private double averageRating;

    public Book(String title, String author, Double averageRating) {
        this.title = title;
        this.author = author;
        this.reviews = new ArrayList<>();
        this.ratings = new ArrayList<>();
        this.averageRating = averageRating != null ? averageRating : 0.0;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public void addReview(Review review) {
        reviews.add(review);
        updateAverageRating();

    }

    public void addRating(int rating) {
        if (rating >= 1 && rating <= 5) {
            ratings.add(rating);
            this.averageRating = getAverageRating(); // Update stored average rating
        } else {
            System.out.println("Invalid rating. Please provide a rating between 1 and 5.");
        }
    }
    public void updateAverageRating() {
        if (reviews.isEmpty()) {
            averageRating = 0.0; // No reviews, so average is 0.0
            System.out.println("No reviews for book: " + title);
        } else {
            int sum = 0;
            for (Review review : reviews) {
                sum += review.getRating(); // Sum all review ratings
            }
            averageRating = (double) sum / reviews.size(); // Calculate the average

        }
    }


    public List<Review> getReviews() {
        return reviews;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public int getNumberOfRatings() {
        return ratings.size();
    }

    public JSONObject toJson() {
        JSONObject bookJson = new JSONObject();
        bookJson.put("title", title);
        bookJson.put("author", author);
        bookJson.put("averageRating", averageRating);

        JSONArray reviewsArray = new JSONArray();
        for (Review review : reviews) {
            JSONObject reviewJson = new JSONObject();
            reviewJson.put("reviewText", review.getReviewText());
            reviewJson.put("rating", review.getRating());
            reviewsArray.add(reviewJson);
        }
        bookJson.put("reviews", reviewsArray);

        return bookJson;
    }
}*/
package org.example;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Book {
    private int bookId;
    private String title;
    private String author;
    private String category;
    private List<Review> reviews;
    private double averageRating;
    private Object publishedDate;

    public Book(String title, String author, double averageRating) {
        this.title = title;
        this.author = author;
        this.averageRating = averageRating;
        this.reviews = new ArrayList<>();
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void addReview(Review review) {
        reviews.add(review);
        updateAverageRating();
    }

    public double getAverageRating() {
        return averageRating;
    }

    private void updateAverageRating() {
        if (reviews.isEmpty()) {
            this.averageRating = 0.0;
        } else {
            int sum = reviews.stream().mapToInt(Review::getRating).sum();
            this.averageRating = sum / (double) reviews.size();
        }
    }

    public void setPublishedDate(LocalDate localDate) {
        this.publishedDate = publishedDate;
    }
}

