package exсeptions;

public class NoServerResponseException extends Exception{
    public NoServerResponseException(String message) {
        super(message);
    }
}
