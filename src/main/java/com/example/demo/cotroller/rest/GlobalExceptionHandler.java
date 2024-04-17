package com.example.demo.cotroller.rest;

import java.nio.file.AccessDeniedException;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.example.demo.model.dto.ErrorDto;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity <ErrorDto> handleException(MethodArgumentNotValidException exception){
        var errors = exception.getAllErrors()
        .stream()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorDto.builder().message(errors).build());
        
    }
    @ExceptionHandler(value = {AccessDeniedException.class,Exception.class})
    public ResponseEntity<Object> handleAccessException(Exception ex, WebRequest request){
        String bodyOfResponse="Access denied - You're unauthorized";
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(bodyOfResponse);
    }
}
