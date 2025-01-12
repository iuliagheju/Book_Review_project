//InvalidUserException.java
package org.example;

public class InvalidUserException extends Exception {
  public InvalidUserException(String message) {
    super(message);
  }
}