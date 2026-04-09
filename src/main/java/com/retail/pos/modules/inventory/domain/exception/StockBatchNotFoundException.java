package com.retail.pos.modules.inventory.domain.exception;

public class StockBatchNotFoundException extends RuntimeException {
    public StockBatchNotFoundException(String message) {
        super(message);
    }
}
