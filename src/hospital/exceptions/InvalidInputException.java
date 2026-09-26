package hospital.exceptions;

/**
 * Thrown when user-supplied data fails a business rule
 * (e.g. empty name, invalid age).
 */
public class InvalidInputException extends Exception {
    public InvalidInputException(String message) {
        super(message);
    }
}
