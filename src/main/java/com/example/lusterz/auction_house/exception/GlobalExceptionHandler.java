package com.example.lusterz.auction_house.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.example.lusterz.auction_house.dao.ErrorDetails;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    // handle all not_found exceptions
    @ExceptionHandler({
        UserException.NotFound.class,
        AuctionItemException.NotFound.class
    })
    public ResponseEntity<ErrorDetails> handleNotFound(RuntimeException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    // handle all already_exists exceptions
    @ExceptionHandler({
        UserException.AlreadyExists.class
    })
    public ResponseEntity<ErrorDetails> handleEmailAlreadyExistsException(RuntimeException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }

    // global handling 
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(RuntimeException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails("An unexpected error occurred", request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
