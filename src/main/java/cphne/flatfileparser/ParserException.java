package cphne.flatfileparser;

public class ParserException extends Exception{

    public ParserException(String message) {
        super(message);
    }

    public ParserException(Throwable cause) {
        super(cause);
    }
}
