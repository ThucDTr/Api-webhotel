package com.hotel.webhotel.exception;

public class UserAlreadyExistException extends RuntimeException{
    
    public UserAlreadyExistException(String message) {
        super(message);
    }
}
