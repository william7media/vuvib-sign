package vuvibsign.shared.exception;

public class SignerServiceException extends Exception {
    public SignerServiceException(String message) {
        super(message);
    }

    public SignerServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
