package com.liaoin.payment.core;

public class PayFailException extends RuntimeException {
    public PayFailException(String message) {
        super(message);
    }
}
