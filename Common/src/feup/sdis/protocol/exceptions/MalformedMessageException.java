package feup.sdis.protocol.exceptions;

/**
 * Malformed message exception
 */
public class MalformedMessageException extends Exception {
    public MalformedMessageException(String message) {
        super (message);
    }
}
