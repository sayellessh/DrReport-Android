package net.swaas.drinfo.core.exception;

/**
 * Created by vinoth on 10/29/15.
 */
public class NetworkException extends RuntimeException {

    private String mMessage;

    public NetworkException(Throwable throwable) {
        super(throwable);
        mMessage = throwable.getMessage();
    }

    public NetworkException(Throwable throwable, String message) {
        super(throwable);
        mMessage = message;
    }

    @Override
    public String getMessage() {
        return mMessage;
    }
}
