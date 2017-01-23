package controllers.businessLogic.hirewand;

/**
 * Created by User on 07-12-2016.
 */
public class InvalidRequestException extends Exception {

    String message = null;
    int code = 0;
    public InvalidRequestException(String message, int code) {
        super(message);
        this.message = message;
        this.code = code;
    }

    public InvalidRequestException(Throwable cause) {
        super(cause);
    }

    @Override
    public String toString() {
        return message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }

}
