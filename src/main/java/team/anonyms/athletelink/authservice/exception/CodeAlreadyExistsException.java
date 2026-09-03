package team.anonyms.athletelink.authservice.exception;

public class CodeAlreadyExistsException extends RuntimeException {
    public CodeAlreadyExistsException(String message) {
        super(message);
    }
}
