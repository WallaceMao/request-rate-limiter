package com.rishiqing.util.http.ratelimiter;

/**
 * @author Wallace Mao
 * Date: 2019-02-21 18:39
 */
public class RequestLockException extends Exception {
    public RequestLockException() {
    }

    public RequestLockException(String message) {
        super(message);
    }

    public RequestLockException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestLockException(Throwable cause) {
        super(cause);
    }

    public RequestLockException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
