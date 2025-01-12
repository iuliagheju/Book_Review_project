package org.example;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;



public class RoleScenes extends Application {
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);
    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showMainMenu();
    }

    private void showMainMenu() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getStyleClass().add("menu-layout");

        Label label = new Label("--- Main Menu ---");
        Button userLoginButton = new Button("Login as User");
        Button adminLoginButton = new Button("Login as Admin");
        Button guestAccessButton = new Button("Guest Access");
        Button newAccountButton = new Button("Create New Account");
        Button exitButton = new Button("Exit");

        userLoginButton.setOnAction(e -> handleUserLogin());
        adminLoginButton.setOnAction(e -> handleAdminLogin());
        guestAccessButton.setOnAction(e -> handleGuestAccess());
        newAccountButton.setOnAction(e -> showCreateAccountDialog());
        exitButton.setOnAction(e -> primaryStage.close());

        userLoginButton.getStyleClass().add("menu-button");
        adminLoginButton.getStyleClass().add("menu-button");
        guestAccessButton.getStyleClass().add("menu-button");
        newAccountButton.getStyleClass().add("menu-button");
        exitButton.getStyleClass().add("menu-button");

        layout.getChildren().addAll(label, userLoginButton, adminLoginButton, guestAccessButton, newAccountButton, exitButton);

        Scene scene = new Scene(layout, 300, 250); // Adjusted height for new button
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.setTitle("Role-Based Menu");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showCreateAccountDialog() {
        // Create dialog layout
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Create New Account");

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        gridPane.add(new Label("Username:"), 0, 0);
        gridPane.add(usernameField, 1, 0);
        gridPane.add(new Label("Password:"), 0, 1);
        gridPane.add(passwordField, 1, 1);
        gridPane.add(new Label("Email:"), 0, 2);
        gridPane.add(emailField, 1, 2);

        dialog.getDialogPane().setContent(gridPane);

        // Add buttons
        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        // Handle create button
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                String username = usernameField.getText();
                String password = passwordField.getText();
                String email = emailField.getText();

                if (validateAccountInput(username, password, email)) {
                    addUserToDatabase(username, password, email);
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void addUserToDatabase(String username, String password, String email) {
        String insertQuery = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";

        Task<Void> addUserTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try (Connection conn = DatabaseConnectionUtil.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

                    stmt.setString(1, username);
                    stmt.setString(2, password);
                    stmt.setString(3, email);
                    stmt.executeUpdate();

                    System.out.println("Executing on thread: " + Thread.currentThread().getName());
                }
                return null;
            }
        };

        addUserTask.setOnSucceeded(e -> showAlert(Alert.AlertType.INFORMATION, "Success", "Account created successfully."));
        addUserTask.setOnFailed(e -> {
            Throwable exception = addUserTask.getException();
            exception.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to create the account.");
        });

        Thread thread = new Thread(addUserTask);
        thread.setDaemon(true);
        thread.start();
    }

    private void handleUserLogin() {
        String[] credentials = promptForCredentials("User Login");
        if (credentials != null) {
            boolean isValid = validateCredentials("users", credentials[0], credentials[1]);
            if (isValid) {
                int userId = fetchUserId(credentials[0]); // Fetch user ID once during login
                showUserMenu(userId); // Pass userId to user-specific menu
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password.");
            }
        }
    }

    private void handleAdminLogin() {
        String[] credentials = promptForCredentials("Admin Login");
        if (credentials != null) {
            boolean isValid = validateCredentials("admins", credentials[0], credentials[1]);
            if (isValid) {
                showAdminMenu();
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid admin username or password.");
            }
        }
    }

    private void handleGuestAccess() {
        showGuestMenu();
    }

    private void showUserMenu(int userId) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getStyleClass().add("user-layout");

        Label label = new Label("--- User Menu ---");
        Button viewReadingListButton = new Button("View Your Reading List");
        Button addBookButton = new Button("Add a Book to Your Reading List");
        Button addReviewButton = new Button("Add a Review");
        Button addRatingButton = new Button("Add a Rating");
        Button logoutButton = new Button("Log Out");

        viewReadingListButton.setOnAction(e -> viewReadingList(userId));
        addBookButton.setOnAction(e -> promptForBookToAddToReadingList(userId));
        addReviewButton.setOnAction(e -> promptForReview(userId));
        addRatingButton.setOnAction(e -> promptForRating());
        logoutButton.setOnAction(e -> showMainMenu());

        viewReadingListButton.getStyleClass().add("user-button");
        addBookButton.getStyleClass().add("user-button");
        addReviewButton.getStyleClass().add("user-button");
        addRatingButton.getStyleClass().add("user-button");
        logoutButton.getStyleClass().add("user-button");


        layout.getChildren().addAll(label, viewReadingListButton, addBookButton, addReviewButton, addRatingButton, logoutButton);

        Scene scene = new Scene(layout, 300, 300);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.setScene(scene);
    }

    private void showAdminMenu() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getStyleClass().add("admin-layout");

        Label label = new Label("--- Admin Menu ---");
        Button addBookButton = new Button("Add a New Book");
        Button addCategoryButton = new Button("Add a New Category");
        Button viewReviewsButton = new Button("View All Reviews");
        Button deleteReviewButton = new Button("Delete Selected Review");
        Button logoutButton = new Button("Log Out");

        viewReviewsButton.setOnAction(e -> viewAllReviews());
        deleteReviewButton.setOnAction(e -> deleteSelectedReview());
        addBookButton.setOnAction(e -> promptForBookDetails());
        addCategoryButton.setOnAction(e -> promptForNewCategory());
        logoutButton.setOnAction(e -> showMainMenu());

        addBookButton.getStyleClass().add("admin-button");
        addCategoryButton.getStyleClass().add("admin-button");
        viewReviewsButton.getStyleClass().add("admin-button");
        deleteReviewButton.getStyleClass().add("admin-button");
        logoutButton.getStyleClass().add("admin-button");

        layout.getChildren().addAll(label, addBookButton, addCategoryButton, viewReviewsButton, deleteReviewButton, logoutButton);

        Scene scene = new Scene(layout, 400, 400);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.setScene(scene);
    }

    private void deleteSelectedReview() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Delete Review");
        dialog.setHeaderText("Enter the Review ID to delete:");
        dialog.setContentText("Review ID:");

        String reviewIdStr = dialog.showAndWait().orElse(null);
        if (reviewIdStr != null) {
            try {
                int reviewId = Integer.parseInt(reviewIdStr);
                deleteReviewFromDatabase(reviewId);
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid numeric Review ID.");
            }
        }
    }

    private void deleteReviewFromDatabase(int reviewId) {
        String query = "DELETE FROM reviews WHERE review_id = ?";

        try (Connection conn = DatabaseConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, reviewId);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Review deleted successfully.");
            } else {
                showAlert(Alert.AlertType.WARNING, "Not Found", "No review found with the given ID.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete the review.");
        }
    }

    private void viewAllReviews() {
        Task<String> viewReviewsTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                String query = "SELECT r.review_id, r.user_id, b.title AS book_title, r.review_text, r.created_at " +
                        "FROM reviews r JOIN books b ON r.book_id = b.book_id";

                StringBuilder reviews = new StringBuilder("--- Reviews ---\n");
                try (Connection conn = DatabaseConnectionUtil.getConnection();
                     Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(query)) {

                    while (rs.next()) {
                        int reviewId = rs.getInt("review_id");
                        int userID = rs.getInt("user_id");
                        String bookTitle = rs.getString("book_title");
                        String reviewText = rs.getString("review_text");
                        String createdAt = rs.getString("created_at");

                        reviews.append("Review ID: ").append(reviewId).append("\n")
                                .append("User ID: ").append(userID).append("\n")
                                .append("Book: ").append(bookTitle).append("\n")
                                .append("Review: ").append(reviewText).append("\n")
                                .append("Date: ").append(createdAt).append("\n\n");
                    }
                }

                // Debugging: Show thread used for database operation
                System.out.println("Executing on thread (Task): " + Thread.currentThread().getName());

                return reviews.toString();
            }
        };

        // Define what happens on success
        viewReviewsTask.setOnSucceeded(e -> {
            //showAlert(Alert.AlertType.INFORMATION, "All Reviews", viewReviewsTask.getValue());
            List<String> reviews = Collections.singletonList(viewReviewsTask.getValue());
            displayReviewsScene(reviews);
        });

        // Define what happens on failure
        viewReviewsTask.setOnFailed(e -> {
            Throwable exception = viewReviewsTask.getException();
            exception.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to retrieve reviews.");
        });

        // Run the task on a separate thread
        Thread thread = new Thread(viewReviewsTask);
        thread.setDaemon(true); // Ensures the thread terminates when the application exits
        thread.start();
    }
    private void displayReviewsScene(List<String> reviews) {
        VBox layout = new VBox(10);
        layout.getStyleClass().add("reviews-container"); // Add a class for styling

        Label title = new Label("View All Reviews");
        title.getStyleClass().add("label-title"); // Styled as a title

        ScrollPane scrollPane = new ScrollPane();
        VBox reviewList = new VBox(10);
        reviewList.getStyleClass().add("list-container"); // Styled container

        // Populate reviews dynamically
        for (String review : reviews) {
            Label reviewLabel = new Label(review);
            reviewLabel.getStyleClass().add("review-item"); // Styled individual review
            reviewList.getChildren().add(reviewLabel);
        }

        scrollPane.setContent(reviewList);
        Button backButton = new Button("Back to Admin Menu");
        backButton.getStyleClass().add("button-back"); // Add a CSS class for styling
        backButton.setOnAction(e -> showAdminMenu());
        layout.getChildren().addAll(title, scrollPane, backButton);

        Scene scene = new Scene(layout, 600, 400);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.setScene(scene);
    }

    private void showGuestMenu() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getStyleClass().add("guest-layout");

        Label label = new Label("--- Guest Menu ---");
        Button viewBooksButton = new Button("View All Books");
        Button searchBooksButton = new Button("Search for a Book");
        Button logoutButton = new Button("Log Out");

        viewBooksButton.setOnAction(e -> viewAllBooks());
        searchBooksButton.setOnAction(e -> searchBooks());
        logoutButton.setOnAction(e -> showMainMenu());

        viewBooksButton.getStyleClass().add("guest-button");
        searchBooksButton.getStyleClass().add("guest-button");
        logoutButton.getStyleClass().add("guest-button");

        layout.getChildren().addAll(label, viewBooksButton, searchBooksButton, logoutButton);

        Scene scene = new Scene(layout, 300, 200);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.setScene(scene);
    }

    private void promptForBookToAddToReadingList(int userId) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Book to Reading List");
        dialog.setHeaderText("Enter the title of the book:");
        dialog.setContentText("Title:");

        String title = dialog.showAndWait().orElse(null);
        if (title != null) {
            addBookToReadingList(title, userId);
        }
    }


    private int fetchUserId(String username) {
        String query = "SELECT user_id FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("user_id");
            } else {
                throw new RuntimeException("User not found: " + username);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching user ID for: " + username);
        }
    }

    private void viewReadingList(int userId) {
        String query = "SELECT b.title, b.author FROM books b " +
                "JOIN readinglist_books rl ON b.book_id = rl.book_id " +
                "WHERE rl.list_id = (SELECT list_id FROM readinglists WHERE user_id = ?)";

        try (Connection conn = DatabaseConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();

            VBox readingListContainer = new VBox(10);
            readingListContainer.getStyleClass().add("readinglist-container");

            while (rs.next()) {
                String title = rs.getString("title");
                String author = rs.getString("author");

                Label bookLabel = new Label(title + " by " + author);
                bookLabel.getStyleClass().add("readinglist-item");

                readingListContainer.getChildren().add(bookLabel);
            }

            if (readingListContainer.getChildren().isEmpty()) {
                Label emptyLabel = new Label("Your reading list is empty.");
                emptyLabel.getStyleClass().add("readinglist-item");
                readingListContainer.getChildren().add(emptyLabel);
            }

            // Add the reading list to a ScrollPane
            ScrollPane scrollPane = new ScrollPane(readingListContainer);
            scrollPane.setFitToWidth(true);


            // Wrap everything in a VBox
            VBox mainContainer = new VBox(10, scrollPane);
            mainContainer.setPadding(new Insets(10));


            Scene scene = new Scene(mainContainer, 400, 500); // Adjust size as needed
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

            Stage stage = new Stage();
            stage.setTitle("Your Reading List");
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to retrieve the reading list.");
        }
    }

    private void addBookToReadingList(String title, int userId) {
        String query = "SELECT book_id FROM books WHERE title = ?";
        String checkListQuery = "SELECT list_id FROM readinglists WHERE user_id = ?";
        String createListQuery = "INSERT INTO readinglists (user_id) VALUES (?)";
        String insertQuery = "INSERT INTO readinglist_books (list_id, book_id) VALUES (?, ?)";

        try (Connection conn = DatabaseConnectionUtil.getConnection();
             PreparedStatement bookStmt = conn.prepareStatement(query);
             PreparedStatement checkListStmt = conn.prepareStatement(checkListQuery);
             PreparedStatement createListStmt = conn.prepareStatement(createListQuery, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {

            // Step 1: Get the book ID
            bookStmt.setString(1, title);
            ResultSet bookRs = bookStmt.executeQuery();

            if (bookRs.next()) {
                int bookId = bookRs.getInt("book_id");

                // Step 2: Check if the user already has a reading list
                checkListStmt.setInt(1, userId);
                ResultSet listRs = checkListStmt.executeQuery();
                int listId;

                if (listRs.next()) {
                    // User already has a reading list
                    listId = listRs.getInt("list_id");
                } else {
                    // Create a new reading list for the user
                    createListStmt.setInt(1, userId);
                    createListStmt.executeUpdate();

                    ResultSet generatedKeys = createListStmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        listId = generatedKeys.getInt(1);
                    } else {
                        throw new RuntimeException("Failed to create a new reading list for the user.");
                    }
                }

                // Step 3: Add the book to the user's reading list
                insertStmt.setInt(1, listId);
                insertStmt.setInt(2, bookId);
                insertStmt.executeUpdate();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Book added to your reading list.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Book not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add the book to the reading list.");
        }
    }


    private void promptForReview(int userId) {
        TextInputDialog titleDialog = new TextInputDialog();
        titleDialog.setTitle("Add Review");
        titleDialog.setHeaderText("Enter the title of the book:");
        titleDialog.setContentText("Title:");

        String title = titleDialog.showAndWait().orElse(null);
        if (title != null) {
            TextInputDialog reviewDialog = new TextInputDialog();
            reviewDialog.setTitle("Add Review");
            reviewDialog.setHeaderText("Enter your review:");
            reviewDialog.setContentText("Review:");

            String review = reviewDialog.showAndWait().orElse(null);
            if (review != null) {
                if (validateReviewInput(title, review)) {
                    addReviewToDatabase(title, review, userId); // Pass the userId here
                }
            }
        }
    }


    private void addReviewToDatabase(String title, String review, int userId) {
        String query = "SELECT book_id FROM books WHERE title = ?";
        String insertQuery = "INSERT INTO reviews (book_id, user_id, review_text) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {

            stmt.setString(1, title);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int bookId = rs.getInt("book_id");

                // Use the passed userId for the review
                insertStmt.setInt(1, bookId);
                insertStmt.setInt(2, userId);
                insertStmt.setString(3, review);
                insertStmt.executeUpdate();

                showAlert(Alert.AlertType.INFORMATION, "Success", "Review added successfully.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Book not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add the review.");
        }
    }


    private void promptForRating() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Rating");
        dialog.setHeaderText("Enter the title of the book:");
        dialog.setContentText("Title:");

        String title = dialog.showAndWait().orElse(null);
        if (title != null) {
            TextInputDialog ratingDialog = new TextInputDialog();
            ratingDialog.setTitle("Add Rating");
            ratingDialog.setHeaderText("Enter your rating (1-5):");
            ratingDialog.setContentText("Rating:");

            String ratingStr = ratingDialog.showAndWait().orElse(null);
            if (ratingStr != null) {
                try {
                    double rating = Double.parseDouble(ratingStr);
                    addRatingToDatabase(title, rating);
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Invalid rating value.");
                }
            }
        }
    }

    private void addRatingToDatabase(String title, double rating) {
        String query = "SELECT book_id, average_rating FROM books WHERE title = ?";
        String updateQuery = "UPDATE books SET average_rating = ? WHERE book_id = ?";

        try (Connection conn = DatabaseConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {

            stmt.setString(1, title);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int bookId = rs.getInt("book_id");
                double currentRating = rs.getDouble("average_rating");
                double newRating = (currentRating + rating) / 2;

                updateStmt.setDouble(1, newRating);
                updateStmt.setInt(2, bookId);
                updateStmt.executeUpdate();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Rating added successfully.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Book not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add the rating.");
        }
    }

    void promptForBookDetails() {
        TextInputDialog titleDialog = new TextInputDialog();
        titleDialog.setTitle("Add New Book");
        titleDialog.setHeaderText("Enter the title of the book:");
        titleDialog.setContentText("Title:");

        String title = titleDialog.showAndWait().orElse(null);
        if (title != null) {
            TextInputDialog authorDialog = new TextInputDialog();
            authorDialog.setTitle("Add New Book");
            authorDialog.setHeaderText("Enter the author of the book:");
            authorDialog.setContentText("Author:");

            String author = authorDialog.showAndWait().orElse(null);
            if (author != null) {
                TextInputDialog categoryDialog = new TextInputDialog();
                categoryDialog.setTitle("Add New Book");
                categoryDialog.setHeaderText("Enter the category of the book:");
                categoryDialog.setContentText("Category:");

                String category = categoryDialog.showAndWait().orElse(null);
                if (category != null) {
                    TextInputDialog publishedDateDialog = new TextInputDialog();
                    publishedDateDialog.setTitle("Add New Book");
                    publishedDateDialog.setHeaderText("Enter the published date of the book (YYYY-MM-DD):");
                    publishedDateDialog.setContentText("Published Date:");

                    String publishedDate = publishedDateDialog.showAndWait().orElse(null);
                    if (publishedDate != null) {
                        TextInputDialog averageRatingDialog = new TextInputDialog();
                        averageRatingDialog.setTitle("Add New Book");
                        averageRatingDialog.setHeaderText("Enter the average rating of the book (e.g., 4.5):");
                        averageRatingDialog.setContentText("Average Rating:");

                        String averageRatingInput = averageRatingDialog.showAndWait().orElse(null);
                        if (averageRatingInput != null) {
                            try {
                                double averageRating = Double.parseDouble(averageRatingInput);
                                addBookToDatabase(title, author, category, publishedDate, averageRating);
                            } catch (NumberFormatException e) {
                                showAlert(Alert.AlertType.ERROR, "Error", "Invalid input for average rating. Please enter a valid number.");
                            }
                        }
                    }
                }
            }
        }
    }

    public void addBookToDatabase(String title, String author, String category, String publishedDate, double averageRating) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Author cannot be null or empty");
        }
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be null or empty");
        }
        if (publishedDate == null || publishedDate.trim().isEmpty()) {
            throw new IllegalArgumentException("Published date cannot be null or empty");
        }
        if (averageRating < 0 || averageRating > 5) {
            throw new IllegalArgumentException("Average rating must be between 0 and 5");
        }

       Task<Void> addBookTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                String insertQuery = "INSERT INTO books (title, author, category, published_date, average_rating) VALUES (?, ?, ?, ?, ?)";

                try (Connection conn = DatabaseConnectionUtil.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

                    stmt.setString(1, title);
                    stmt.setString(2, author);
                    stmt.setString(3, category);
                    stmt.setString(4, publishedDate);
                    stmt.setDouble(5, averageRating);

                    stmt.executeUpdate();
                }

                // Debugging: Show thread used for database operation
                System.out.println("Executing on thread (Task): " + Thread.currentThread().getName());
                return null;
            }
        };

        // Define what happens on success
        addBookTask.setOnSucceeded(e -> {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Book added successfully.");

        });

        // Define what happens on failure
        addBookTask.setOnFailed(e -> {
            Throwable exception = addBookTask.getException();
            exception.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add the book.");
        });

        // Run the task on a separate thread
        Thread thread = new Thread(addBookTask);
        thread.setDaemon(true); // Ensures the thread terminates when the application exits
        thread.start();
    }


    @Override
    public void stop() throws Exception {
        super.stop();
        executorService.shutdown();
    }

    private void promptForNewCategory() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add New Category");
        dialog.setHeaderText("Enter the name of the new category:");
        dialog.setContentText("Category:");

        String category = dialog.showAndWait().orElse(null);
        if (category != null) {
            addCategoryToDatabase(category);
        }
    }

    void addCategoryToDatabase(String category) {
        Task<Void> addCategoryTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                String insertQuery = "INSERT INTO categories (name) VALUES (?)";

                try (Connection conn = DatabaseConnectionUtil.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

                    stmt.setString(1, category);
                    stmt.executeUpdate();

                    // Debugging: Show thread used for database operation
                    System.out.println("Executing on thread (Task): " + Thread.currentThread().getName());
                }
                return null;
            }
        };

        // Define what happens on success
        addCategoryTask.setOnSucceeded(e -> {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Category added successfully.");
        });

        // Define what happens on failure
        addCategoryTask.setOnFailed(e -> {
            Throwable exception = addCategoryTask.getException();
            exception.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add the category.");
        });

        // Run the task on a separate thread
        Thread thread = new Thread(addCategoryTask);
        thread.setDaemon(true); // Ensures the thread terminates when the application exits
        thread.start();
    }

    void viewAllBooks() {
        String query = "SELECT title, author, average_rating FROM books";
        try (Connection conn = DatabaseConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();
            VBox booksContainer = new VBox(10);
            booksContainer.setAlignment(Pos.CENTER);
            booksContainer.getStyleClass().add("books-container");

            while (rs.next()) {
                String book = rs.getString("title") + " by " + rs.getString("author") +
                        " - Rating: " + rs.getDouble("average_rating");

                Label bookLabel = new Label(book);
                bookLabel.getStyleClass().add("book-item");
                booksContainer.getChildren().add(bookLabel);
            }

            if (booksContainer.getChildren().isEmpty()) {
                Label emptyLabel = new Label("No books available.");
                emptyLabel.getStyleClass().add("empty-message");
                booksContainer.getChildren().add(emptyLabel);
            }

            // Back button
            Button backButton = new Button("Back");
            backButton.setOnAction(e -> ((Stage) backButton.getScene().getWindow()).close()); // Close the current window
            backButton.getStyleClass().add("back-button");

            booksContainer.getChildren().add(backButton);

            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setContent(booksContainer);
            scrollPane.setFitToWidth(true);
            scrollPane.setPannable(true);
            // Scene setup
            Scene booksScene = new Scene(scrollPane, 600, 400);
            booksScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

            // New Stage for the book list
            Stage booksStage = new Stage();
            booksStage.setTitle("All Books");
            booksStage.setScene(booksScene);
            booksStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to retrieve the books.");
        }
    }

    private void searchBooks() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search Books");
        dialog.setHeaderText("Enter the book title or author:");
        dialog.setContentText("Search:");

        String searchTerm = dialog.showAndWait().orElse(null);
        if (searchTerm != null) {
            String query = "SELECT title, author FROM books WHERE title LIKE ? OR author LIKE ?";
            try (Connection conn = DatabaseConnectionUtil.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, "%" + searchTerm + "%");
                stmt.setString(2, "%" + searchTerm + "%");

                ResultSet rs = stmt.executeQuery();
                List<String> books = new ArrayList<>();

                while (rs.next()) {
                    String book = rs.getString("title") + " by " + rs.getString("author");
                    books.add(book);
                }

                if (books.isEmpty()) {
                    showAlert(Alert.AlertType.INFORMATION, "Search Results", "No books found.");
                } else {
                    showAlert(Alert.AlertType.INFORMATION, "Search Results", String.join("\n", books));
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to search for books.");
            }
        }
    }

    private String[] promptForCredentials(String title) {
        TextInputDialog usernameDialog = new TextInputDialog();
        usernameDialog.setTitle(title);
        usernameDialog.setHeaderText("Enter your username:");
        usernameDialog.setContentText("Username:");
        String username = usernameDialog.showAndWait().orElse(null);

        if (username != null) {
            TextInputDialog passwordDialog = new TextInputDialog();
            passwordDialog.setTitle(title);
            passwordDialog.setHeaderText("Enter your password:");
            passwordDialog.setContentText("Password:");
            String password = passwordDialog.showAndWait().orElse(null);

            if (password != null) {
                return new String[]{username, password};
            }
        }
        return null;
    }

    boolean validateCredentials(String table, String username, String password) {
        String query = "SELECT * FROM " + table + " WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean validateAccountInput(String username, String password, String email) {
        if (username == null || username.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Username cannot be empty.");
            return false;
        }
        if (password == null || password.length() < 6) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Password must be at least 6 characters long.");
            return false;
        }
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Invalid email address.");
            return false;
        }
        return true;
    }
    private boolean validateReviewInput(String title, String reviewText) {
        if (title == null || title.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Book title cannot be empty.");
            return false;
        }
        if (reviewText == null || reviewText.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Review text cannot be empty.");
            return false;
        }
        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
