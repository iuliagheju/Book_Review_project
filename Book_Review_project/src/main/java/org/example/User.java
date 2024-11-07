package org.example;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public abstract class User implements Savable, Comparable<User> {
    private String username;
    private ArrayList<Book> booksRead; // List of books the user has read
    private ArrayList<ArrayList<Review>> reviews; // List of reviews for each book

    public User(String username) {
        this.username = username;
        this.booksRead = new ArrayList<>();
        this.reviews = new ArrayList<>();
    }

    public void addRating(Book book, int rating) {
        book.setRating(rating);
        booksRead.add(book);
    }

    public ArrayList<Book> getBooksRead() {
        return booksRead;
    }

    public Collection<Review> getReviews() {
        ArrayList<Review> allReviews = new ArrayList<>();
        for (ArrayList<Review> bookReviews : reviews) {
            allReviews.addAll(bookReviews);
        }
        return allReviews;
    }

    public void addReview(int index, Review newReview) {
        // Ensure index is within bounds
        if (index >= 0 && index < booksRead.size()) {
            // If this book doesn't have any reviews yet, initialize a new list for it
            while (reviews.size() <= index) {
                reviews.add(new ArrayList<>());
            }
            reviews.get(index).add(newReview);
        } else {
            System.out.println("Invalid index. No such book in the user's reading list.");
        }
    }

    // Returns a specific book by index in the booksRead list
    public Book getBook(int index) {
        if (index >= 0 && index < booksRead.size()) {
            return booksRead.get(index);
        }
        return null;
    }

    public String getUsername() {
        return username;

    }
}