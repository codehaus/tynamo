package org.trails.spring.util;

/**
 * Exception class for handling error situations in <link>ReflectionUtils</link>.
 * 
 * @author Jurjan Woltman
 */
public class ReflectionUtilsException extends RuntimeException {
  
  /** The serialVersionUID. */
  private static final long serialVersionUID = -4575084200351173545L;

  /**
   * Constructs a new instance.
   * 
   * @param message the message with an explanation
   * @param cause the root cause
   */
  public ReflectionUtilsException(String message, Throwable cause) {
    super(message, cause);
  }

}
