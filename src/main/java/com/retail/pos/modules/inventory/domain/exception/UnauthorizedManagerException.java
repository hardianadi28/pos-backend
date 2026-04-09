package com.retail.pos.modules.inventory.domain.exception;

public class UnauthorizedManagerException extends RuntimeException {
    public UnauthorizedManagerException(String message) {
        super(message);
    }
}
