package com.example.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<String> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException exception){

        return new ResponseEntity<>(exception.getBindingResult().getAllErrors().get(0).getDefaultMessage(), HttpStatus.BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({InvalidInputException.class})
    public ResponseEntity<String> invalidInputExceptionHandler(InvalidInputException exception){

        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
