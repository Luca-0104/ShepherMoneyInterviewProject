package com.shepherdmoney.interviewproject.exceptionhandler;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.BindException;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * handle all sorts of exceptions
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<String> handelGlobalException(Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error happens, please try again latter.");
    }


    /**
     * handle the invalid argument exception, e.g. the invalid email format in the payload vo
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

}
