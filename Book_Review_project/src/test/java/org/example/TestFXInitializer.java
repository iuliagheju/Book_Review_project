package org.example;
import javafx.application.Application;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;

public class TestFXInitializer extends Application {

    @Override
    public void start(Stage primaryStage) {
        // No-op for headless JavaFX environment
    }

    @BeforeAll
    public static void initializeJavaFX() {
        new Thread(() -> Application.launch(TestFXInitializer.class)).start();
        try {
            Thread.sleep(1000); // Wait for JavaFX runtime to initialize
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
