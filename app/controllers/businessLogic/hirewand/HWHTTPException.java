package controllers.businessLogic.hirewand;

/**
 * Created by User on 07-12-2016.
 */
public class HWHTTPException extends Exception {

    String message = null;
    int code = 0;
    public HWHTTPException(String message, int code) {
        super(message);
        this.message = message;
        this.code = code;
    }

    public HWHTTPException(Throwable cause) {
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
