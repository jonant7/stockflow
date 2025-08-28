package com.stockflow.core.shared.exception;


public class BusinessException extends RuntimeException {

    private final String code;
    private final Object[] args;

    public BusinessException(String code) {
        super(code);
        this.code = code;
        this.args = new Object[0];
    }

    public BusinessException(String code, Object... args) {
        super(code);
        this.code = code;
        this.args = args == null ? new Object[0] : args;
    }

    public BusinessException(String code, Throwable cause, Object... args) {
        super(code, cause);
        this.code = code;
        this.args = args == null ? new Object[0] : args;
    }

    public Object[] getArgs() {
        return args.clone();
    }
}