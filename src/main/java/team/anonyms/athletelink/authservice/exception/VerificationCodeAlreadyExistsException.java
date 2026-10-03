package team.anonyms.athletelink.authservice.exception;

public class VerificationCodeAlreadyExistsException extends RuntimeException {
    public VerificationCodeAlreadyExistsException(String message) {
        super(message);
    }
}
