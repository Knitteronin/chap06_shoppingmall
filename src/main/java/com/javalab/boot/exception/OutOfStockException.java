package com.javalab.boot.exception;

/**
 * 사용자가 만드는 커스텀 예외 - 재고 부족 예외
 */
public class OutOfStockException extends RuntimeException{

    public OutOfStockException(String message) {
        super(message);
    }

}