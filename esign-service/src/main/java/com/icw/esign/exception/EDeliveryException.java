package com.icw.esign.exception;


public class EDeliveryException extends Exception {
    public EDeliveryException(String msg) {
        super(msg);
    }

    public EDeliveryException(String msg, Throwable cause) {
        super(msg, cause);
    }
}