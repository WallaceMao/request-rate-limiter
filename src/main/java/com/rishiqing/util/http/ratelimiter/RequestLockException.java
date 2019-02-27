package com.rishiqing.util.http.ratelimiter;

/**
 * @author Wallace Mao
 * Date: 2019-02-21 18:39
 */
public class RequestLockException extends Exception {
    public RequestLockException(Throwable cause) {
        super(cause);
    }
}
