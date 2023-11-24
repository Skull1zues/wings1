package com.example.winngs;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.nio.file.AccessDeniedException;
import java.util.Date;

@ControllerAdvice
public class Exception {
    @ExceptionHandler(java.lang.Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception exception,
                                                   WebRequest webRequest){

        return new ResponseEntity<>( HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException exception,
                                                                    WebRequest webRequest){

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
