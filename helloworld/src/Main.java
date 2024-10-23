public class Main {
    public static void main(String[] args) {
        inputDevice InputDevice = new inputDevice();
        OutputDevice outputdevice = new OutputDevice();
        Application app = new Application(InputDevice, outputdevice);

        // Example User and Book for review purposes
        Book book = new Book("The Great Gatsby");
        User user = new User("JohnDoe", book, 3);  // User with a book and space for 3 reviews

        if (args.length > 0) {
            String firstArg = args[0];

            switch (firstArg) {
                case "words":
                    // Run exampleHistogram function if the first argument is 'words'
                    exampleHistogram(app, InputDevice, outputdevice);
                    break;

                case "numbers":
                    // Run randomArraySort function if the first argument is 'numbers'
                    app.randomArraySort();
                    outputdevice.writeMessage("Array sorted successfully.");
                    break;

                case "play":
                    // Run the playGame function if the first argument is 'play'
                    if (args.length > 1) {
                        try {
                            int rounds = Integer.parseInt(args[1]);
                            app.playGame();
                        } catch (NumberFormatException e) {
                            outputdevice.writeMessage("The second argument must be a valid integer for the number of rounds.");
                        }
                    } else {
                        outputdevice.writeMessage("The 'play' command requires a second argument for the number of rounds.");
                    }
                    break;

                case "addReview":
                    // Run addReview function if the first argument is 'addReview'
                    if (args.length > 2) {
                        int index = Integer.parseInt(args[1]);
                        String reviewText = args[2];
                        app.addReview(index, user, reviewText);
                        // Display the reviews immediately after adding one
                        outputdevice.writeMessage("Review added. Current reviews:");
                        app.displayBookRating(user);
                    } else {
                        outputdevice.writeMessage("The 'addReview' command requires two arguments: index and reviewText.");
                    }
                    break;


                case "rateBook":
                    if (args.length > 1) {
                        try {
                            int rating = Integer.parseInt(args[1]);
                            app.rateBook(user, rating);
                            outputdevice.writeMessage("Current book rating:");
                            app.displayUserReviews(user);
                            app.displayBookRating(user); // Display current rating after rating
                        } catch (NumberFormatException e) {
                            outputdevice.writeMessage("Invalid rating format. Please provide a number between 1 and 5.");
                        }
                    } else {
                        outputdevice.writeMessage("The 'rateBook' command requires a rating (between 1 and 5).");
                    }
                    break;

                case "displayReviews":
                    app.displayUserReviews(user);
                    app.displayBookRating(user);  // Also display the rating when showing reviews
                    break;

                default:
                    outputdevice.writeMessage("Invalid first argument. Please use 'words', 'numbers', 'play', 'addReview', or 'displayReviews'.");
                    break;
            }
        } else {
            outputdevice.writeMessage("No arguments provided.");
        }
    }

    private static void exampleHistogram(Application app, inputDevice InputDevice, OutputDevice outputdevice) {
        String sentence = inputDevice.getLine();
        int[] histogram = app.wordSizeHistogram(sentence);

        outputdevice.writeMessage("Word size histogram:");
        for (int i = 0; i < histogram.length; i++) {
            outputdevice.writeMessage("Word length " + i + ": " + histogram[i]);
        }
    }
}
