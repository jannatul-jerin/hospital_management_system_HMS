package hospital.exceptions;

/**
 * Thrown when an update, delete, or lookup targets an ID
 * that does not exist in storage.
 */
public class RecordNotFoundException extends Exception {
    public RecordNotFoundException(String message) {
        super(message);
    }
}
