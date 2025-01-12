//InputDevice.java
package org.example;

import java.util.Scanner;

public class InputDevice {
    private static Scanner scanner = new Scanner(System.in);

    // Reads an integer input from the user
    public int nextInt() {
        while (!scanner.hasNextInt()) {
            System.out.println("Please enter a valid number.");
            scanner.next(); // discard invalid input
        }
        return scanner.nextInt();
    }

    // Reads an entire line from user input
    public String nextLine() {
        return scanner.nextLine();
    }
}
