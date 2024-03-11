package com.hotel.webhotel.exception;

public class InternalServerException extends RuntimeException {
    public InternalServerException(String message){
        super(message);
    }
}
