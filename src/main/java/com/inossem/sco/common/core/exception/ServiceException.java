package com.inossem.sco.common.core.exception;

public class ServiceException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private Integer code;
    private String message;
    private String detailMessage;

    public ServiceException() {
        this.code = 500;
    }

    public ServiceException(String message) {
        this.code = 500;
        this.message = message;
    }

    public ServiceException(String message, Integer code) {
        this.message = message;
        this.code = code;
    }

    public String getDetailMessage() {
        return this.detailMessage;
    }

    public String getMessage() {
        return this.message;
    }

    public Integer getCode() {
        return this.code;
    }

    public ServiceException setMessage(String message) {
        this.message = message;
        return this;
    }

    public ServiceException setDetailMessage(String detailMessage) {
        this.detailMessage = detailMessage;
        return this;
    }
}
