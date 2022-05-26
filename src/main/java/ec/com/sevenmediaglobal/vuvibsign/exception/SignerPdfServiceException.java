package ec.com.sevenmediaglobal.vuvibsign.exception;

public class SignerPdfServiceException extends Exception {
    public SignerPdfServiceException(String message) {
        super(message);
    }

    public SignerPdfServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
