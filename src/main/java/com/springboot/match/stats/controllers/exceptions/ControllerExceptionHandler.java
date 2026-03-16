package com.springboot.match.stats.controllers.exceptions;

import com.springboot.match.stats.services.exceptions.InactiveMapException;
import com.springboot.match.stats.services.exceptions.MapAlreadyExistsException;
import com.springboot.match.stats.services.exceptions.NicknameAlreadyExistsException;
import com.springboot.match.stats.services.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<StandardError> messageNotReadableHandler(HttpServletRequest request) {
        String error = "Body request error";
        String message = "Verify JSON request again";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardError err = new StandardError(Instant.now(), status.value(), error, message, request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> resourceNotFoundHandler(HttpServletRequest request) {
        String error = "Resource not found error";
        String message = "Verify if the resource exists";
        HttpStatus status = HttpStatus.NOT_FOUND;
        StandardError err = new StandardError(Instant.now(), status.value(), error, message, request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(NicknameAlreadyExistsException.class)
    public ResponseEntity<StandardError> nicknameAlreadyExistsHandler(HttpServletRequest request) {
        String error = "Nickname already exists";
        String message = "This nickname already exists, choose another one";
        HttpStatus status = HttpStatus.CONFLICT;
        StandardError err = new StandardError(Instant.now(), status.value(), error, message, request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(MapAlreadyExistsException.class)
    public ResponseEntity<StandardError> mapAlreadyExistsHandler(HttpServletRequest request) {
        String error = "Map already exists";
        String message = "This map already exists, choose another one";
        HttpStatus status = HttpStatus.CONFLICT;
        StandardError err = new StandardError(Instant.now(), status.value(), error, message, request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(InactiveMapException.class)
    public ResponseEntity<StandardError> inactiveMapHandler(HttpServletRequest request) {
        String error = "Map is inactive";
        String message = "This map is inactive, choose another one";
        HttpStatus status = HttpStatus.UNPROCESSABLE_CONTENT;
        StandardError err = new StandardError(Instant.now(), status.value(), error, message, request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardError> generalExceptionHandler(Exception e, HttpServletRequest request) {
        String error = "Internal application error";
        String message = "Internal application error";
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        StandardError err = new StandardError(Instant.now(), status.value(), error, message, request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }
}
