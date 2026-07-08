package com.elite.exceptions;

import com.elite.response.APIResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<APIResponse> handleUserAlreadyExistsException(UserAlreadyExistsException e, HttpServletRequest request) {
        APIResponse apiResponse = APIResponse
                .builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error(e.getMessage())
                .message("User already exists")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiResponse);
    }



    @ExceptionHandler(UserNotAuthenticatedException.class)
    public ResponseEntity<APIResponse> handleUserNotAuthenticatedException(UserNotAuthenticatedException e, HttpServletRequest request) {
        APIResponse apiResponse = APIResponse.builder().timestamp(LocalDateTime.now()).status(HttpStatus.UNAUTHORIZED.value()).error(e.getMessage())
                .path(request.getRequestURI())
                .message("User Not Authenticated").build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
    }


    @ExceptionHandler(InvoiceNotFoundException.class)
    public ResponseEntity<APIResponse> handleInvoiceNotFoundException(InvoiceNotFoundException e, HttpServletRequest request) {
        APIResponse apiResponse = APIResponse.builder().timestamp(LocalDateTime.now()).status(HttpStatus.NOT_FOUND.value()).error(e.getMessage())
                .path(request.getRequestURI())
                .message("Invoice not found").build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
    }


}
