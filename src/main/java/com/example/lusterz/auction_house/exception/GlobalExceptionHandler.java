package com.example.lusterz.auction_house.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.example.lusterz.auction_house.dao.ErrorDetails;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    // handle all not_found exceptions
    @ExceptionHandler({
        UserException.NotFound.class,
        AuctionItemException.NotFound.class,
        BidException.NotFound.class
    })
    public ResponseEntity<ErrorDetails> handleNotFound(RuntimeException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    // handle all already_exists exceptions
    @ExceptionHandler({
        UserException.AlreadyExists.class
    })
    public ResponseEntity<ErrorDetails> handleAlreadyExists(RuntimeException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }

    // handle all bad_request exceptions
    @ExceptionHandler({
        UserException.InsufficientFunds.class,
        AuctionItemException.InvalidState.class,
        BidException.InsufficientBid.class,
    })
    public ResponseEntity<ErrorDetails> handleBadRequest(RuntimeException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    // handle all unauthorized excetptions
    @ExceptionHandler({
        UserException.Unauthorized.class,
        AuctionItemException.Unauthorized.class,
        BidException.Unauthorized.class
    })
    public ResponseEntity<ErrorDetails> handleUnauthorized(RuntimeException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    // handle multiple users bidding at the same time exception
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorDetails> handleBidConflict(WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails("Someone else just placed a bid. Please refresh for the latest price.", request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }

    // global handling 
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(RuntimeException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails("An unexpected error occurred", request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
