package com.stationery.inventory.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice          
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class) //Specific exception is catched and custom response is returned.
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();  //Errors store karne ke liye.
        ex.getBindingResult().getAllErrors().forEach((error) -> {  //saari error nikalta h
            String fieldName = ((FieldError) error).getField();    //field name nikalta h
            String errorMessage = error.getDefaultMessage();  //error message nikalta h
            errors.put(fieldName, errorMessage);        //field name aur error message ko map me store karta h
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST); //400 status code return karta h
    }

    @ExceptionHandler(RuntimeException.class) //RuntimeException ke liye handle karo
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) { 
        Map<String, String> error = new HashMap<>(); 
        error.put("error", ex.getMessage()); //error message ko map me store karta h
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST); 
    }
}
