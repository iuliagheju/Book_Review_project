package org.example;
import java.util.Scanner;

public class Main {
    private static Application app;
    private static User currentUser;

    public static void main(String[] args) {
        // Initialize InputDevice and OutputDevice for handling input/output
        InputDevice inputDevice = new InputDevice();
        OutputDevice outputDevice = new OutputDevice();
        app = new Application(inputDevice, outputDevice);

        Scanner scanner = new Scanner(System.in);

        // Display main menu once
        outputDevice.writeMessage("\n--- Main Menu ---");
        outputDevice.writeMessage("1. Log in");
        outputDevice.writeMessage("2. View all books");
        outputDevice.writeMessage("3. Search for a book");
        outputDevice.writeMessage("4. Exit");
        outputDevice.writeMessage("Choose an option: ");

        int choice = -1;

        if (scanner.hasNextInt()) {
            choice = scanner.nextInt();
            scanner.nextLine(); // consume the newline left after nextInt
        } else {
            outputDevice.writeMessage("Invalid choice. Please restart the application and enter a valid number.");
            scanner.close();
            return;
        }

        switch (choice) {
            case 1:
                login(scanner, outputDevice);
                break;
            case 2:
                viewAllBooks();
                break;
            case 3:
                searchBook(scanner, outputDevice, false); // Non-logged-in user
                break;
            case 4:
                outputDevice.writeMessage("Exiting...");
                scanner.close();
                return;
            default:
                outputDevice.writeMessage("Invalid choice. Please restart the application and choose a valid option.");
                scanner.close();
                return;
        }

        scanner.close(); // Close scanner after handling the choice
    }

    private static void login(Scanner scanner, OutputDevice outputDevice) {
        outputDevice.writeMessage("Enter username: ");
        String username = scanner.nextLine();
        currentUser = new User(username) {
            @Override
            public int compareTo(User o) {
                return 0;
            }

            @Override
            public void save() {

            }
        };
        //app.addUser(currentUser); // Add the user to Application

        outputDevice.writeMessage("Welcome, " + username + "!");

        // Display user-specific options after login
        userMenu(scanner, outputDevice);
    }

    private static void viewAllBooks() {
        app.displayBooks(); // Assumes Application has a method to display all books
    }

    private static void userMenu(Scanner scanner, OutputDevice outputDevice) {
        outputDevice.writeMessage("\n--- User Menu ---");
        outputDevice.writeMessage("1. Search for a book");
        outputDevice.writeMessage("2. Log out");
        outputDevice.writeMessage("Choose an option: ");

        int choice = -1;

        if (scanner.hasNextInt()) {
            choice = scanner.nextInt();
            scanner.nextLine(); // consume the newline left after nextInt
        } else {
            outputDevice.writeMessage("Invalid choice. Please restart the application and enter a valid number.");
            return;
        }

        switch (choice) {
            case 1:
                searchBook(scanner, outputDevice, true); // Logged-in user
                break;
            case 2:
                outputDevice.writeMessage("Logging out...");
                currentUser = null; // Clear the current user
                break;
            default:
                outputDevice.writeMessage("Invalid choice. Please restart the application and choose a valid option.");
        }
    }

    private static void searchBook(Scanner scanner, OutputDevice outputDevice, boolean isLoggedIn) {
        outputDevice.writeMessage("\n--- Search Book ---");
        outputDevice.writeMessage("1. Search by Title");
        outputDevice.writeMessage("2. Search by Author");
        outputDevice.writeMessage("3. Search by Title and Author");
        outputDevice.writeMessage("Choose an option: ");

        int choice = -1;

        if (scanner.hasNextInt()) {
            choice = scanner.nextInt();
            scanner.nextLine(); // consume the newline left after nextInt
        } else {
            outputDevice.writeMessage("Invalid choice.");
            return;
        }

        outputDevice.writeMessage("Enter search query: ");
        String query = scanner.nextLine();

        Book foundBook = null;
        switch (choice) {
            case 1:
                foundBook = app.searchBookByTitle(query);
                break;
            case 2:
                foundBook = app.searchBookByAuthor(query);
                break;
            case 3:
                outputDevice.writeMessage("Enter author: ");
                String authorQuery = scanner.nextLine();
                foundBook = app.searchBookByTitleAndAuthor(query, authorQuery);
                break;
            default:
                outputDevice.writeMessage("Invalid choice.");
                return;
        }

        if (foundBook != null) {
            outputDevice.writeMessage("Book found: " + foundBook.getTitle() + " by " + foundBook.getAuthor());
            displayBookOptions(scanner, outputDevice, foundBook, isLoggedIn);
        } else {
            outputDevice.writeMessage("Book not found.");
        }
    }

    private static void displayBookOptions(Scanner scanner, OutputDevice outputDevice, Book book, boolean isLoggedIn) {
        outputDevice.writeMessage("\n--- Book Options ---");
        outputDevice.writeMessage("1. View all reviews and average rating");
        if (isLoggedIn) {
            outputDevice.writeMessage("2. Add a review");
            outputDevice.writeMessage("3. Add a rating");
            outputDevice.writeMessage("4. Go back to previous menu");
        } else {
            outputDevice.writeMessage("2. Go back to previous menu");
        }
        outputDevice.writeMessage("Choose an option: ");

        int choice = -1;

        if (scanner.hasNextInt()) {
            choice = scanner.nextInt();
            scanner.nextLine(); // consume the newline left after nextInt
        } else {
            outputDevice.writeMessage("Invalid choice.");
            return;
        }

        if (choice == 1) {
            viewBookReviews(outputDevice, book);
        } else if (isLoggedIn) {
            switch (choice) {
                case 2:
                    addReview(scanner, outputDevice, book);
                    break;
                case 3:
                    addRating(scanner, outputDevice, book);
                    break;
                case 4:
                    return;
                default:
                    outputDevice.writeMessage("Invalid choice. Please try again.");
            }
        } else {
            if (choice == 2) {
                return;
            } else {
                outputDevice.writeMessage("Invalid choice. Please try again.");
            }
        }
    }

    private static void addReview(Scanner scanner, OutputDevice outputDevice, Book book) {
        outputDevice.writeMessage("Enter your review: ");
        String reviewText = scanner.nextLine();
        outputDevice.writeMessage("Enter a rating for the review (1-5): ");
        int rating = scanner.nextInt();
        scanner.nextLine(); // consume newline

        Review review = new Review(reviewText, rating);
        currentUser.addReview(0, review);
        outputDevice.writeMessage("Review added successfully.");
    }

    private static void addRating(Scanner scanner, OutputDevice outputDevice, Book book) {
        outputDevice.writeMessage("Enter your rating for the book (1-5): ");
        int rating = scanner.nextInt();
        scanner.nextLine(); // consume newline

        book.addRating(rating);
        outputDevice.writeMessage("Rating added successfully.");
    }

    private static void viewBookReviews(OutputDevice outputDevice, Book book) {
        outputDevice.writeMessage("\n--- Book Reviews ---");
        outputDevice.writeMessage("Book: " + book.getTitle() + " by " + book.getAuthor());
        outputDevice.writeMessage("Average Rating: " + book.getAverageRating());

        if (book.getReviews().isEmpty()) {
            outputDevice.writeMessage("No reviews available.");
        } else {
            for (Review review : book.getReviews()) {
                outputDevice.writeMessage("- " + review.getReviewText() + " (Rating: " + review.getRating() + ")");
            }
        }
    }
}
