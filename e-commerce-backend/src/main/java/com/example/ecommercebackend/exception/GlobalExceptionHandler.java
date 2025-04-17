package com.example.ecommercebackend.exception;

import com.example.ecommercebackend.dto.APIExceptionResponse;
import com.example.ecommercebackend.exception.custom.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<APIExceptionResponse> handleResourceNotFoundException(ResourceNotFoundException e) {
        return new ResponseEntity<>(new APIExceptionResponse(e.getMessage(), false), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return new ResponseEntity<>(new APIExceptionResponse(e.getMessage(), false), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<APIExceptionResponse> handleRuntimeException(RuntimeException e) {
        return new ResponseEntity<>(new APIExceptionResponse(e.getMessage(), false), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
