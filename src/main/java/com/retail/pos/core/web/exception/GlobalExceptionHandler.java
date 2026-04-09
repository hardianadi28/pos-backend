package com.retail.pos.core.web.exception;

import com.retail.pos.core.web.response.WebResponse;
import com.retail.pos.modules.auth.domain.exception.InvalidCredentialsException;
import com.retail.pos.modules.auth.domain.exception.UserInactiveException;
import com.retail.pos.modules.inventory.domain.exception.CategoryNotFoundException;
import com.retail.pos.modules.inventory.domain.exception.DuplicateProductException;
import com.retail.pos.modules.inventory.domain.exception.InvalidPriceException;
import com.retail.pos.modules.inventory.domain.exception.ProductNotFoundException;
import com.retail.pos.modules.user.domain.exception.DuplicateUsernameException;
import com.retail.pos.modules.user.domain.exception.RoleNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidPriceException.class)
    public ResponseEntity<WebResponse<Object>> handleInvalidPriceException(InvalidPriceException e) {
        log.warn("Invalid Price: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(WebResponse.error(e.getMessage(), null));
    }

    @ExceptionHandler(DuplicateProductException.class)
    public ResponseEntity<WebResponse<Object>> handleDuplicateProductException(DuplicateProductException e) {
        log.warn("Duplicate Product: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(WebResponse.error(e.getMessage(), null));
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<WebResponse<Object>> handleProductNotFoundException(ProductNotFoundException e) {
        log.warn("Product Not Found: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(WebResponse.error(e.getMessage(), null));
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<WebResponse<Object>> handleCategoryNotFoundException(CategoryNotFoundException e) {
        log.warn("Category Not Found: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(WebResponse.error(e.getMessage(), null));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<WebResponse<Object>> handleInvalidCredentialsException(InvalidCredentialsException e) {
        log.warn("Invalid Credentials: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(WebResponse.error(e.getMessage(), null));
    }

    @ExceptionHandler(UserInactiveException.class)
    public ResponseEntity<WebResponse<Object>> handleUserInactiveException(UserInactiveException e) {
        log.warn("User Inactive: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(WebResponse.error(e.getMessage(), null));
    }

    @ExceptionHandler(DuplicateUsernameException.class)
    public ResponseEntity<WebResponse<Object>> handleDuplicateUsernameException(DuplicateUsernameException e) {
        log.warn("Duplicate Username Exception: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(WebResponse.error(e.getMessage(), null));
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<WebResponse<Object>> handleRoleNotFoundException(RoleNotFoundException e) {
        log.warn("Role Not Found Exception: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(WebResponse.error(e.getMessage(), null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<WebResponse<Map<String, String>>> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.warn("Validation Exception: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(WebResponse.error("Validation failed", errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<WebResponse<Object>> handleGeneralException(Exception e) {
        log.error("Unhandled Exception: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(WebResponse.error("An unexpected error occurred: " + e.getMessage(), null));
    }
}
