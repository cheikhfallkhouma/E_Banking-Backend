package com.cfk.ebankingbanking.exceptions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler({CustomerNotFoundException.class, BankAccountNotFoundException.class})
    public @ResponseBody ResponseEntity<String> handleExceptionNotFound(Exception e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler({BalanceNotSufficientException.class})
    public @ResponseBody ResponseEntity<String> handleExceptionNotSufficient(Exception e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(CustomerDeleteException.class)
    public ResponseEntity<Object> handleCustomerDeleteException(CustomerDeleteException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /*@ExceptionHandler({BankAccountNotFoundException.class})
    public @ResponseBody String handleException(BankAccountNotFoundException ex){
        return ex.getMessage();
    }*/
}
