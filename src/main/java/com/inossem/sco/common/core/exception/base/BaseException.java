package com.inossem.sco.common.core.exception.base;


public class BaseException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private String module;
    private String code;
    private Object[] args;
    private String defaultMessage;

    public BaseException(String module, String code, Object[] args, String defaultMessage) {
        this.module = module;
        this.code = code;
        this.args = args;
        this.defaultMessage = defaultMessage;
    }

    public BaseException(String module, String code, Object[] args) {
        this(module, code, args, (String)null);
    }

    public BaseException(String module, String defaultMessage) {
        this(module, (String)null, (Object[])null, defaultMessage);
    }

    public BaseException(String code, Object[] args) {
        this((String)null, code, args, (String)null);
    }

    public BaseException(String defaultMessage) {
        this((String)null, (String)null, (Object[])null, defaultMessage);
    }

    public String getModule() {
        return this.module;
    }

    public String getCode() {
        return this.code;
    }

    public Object[] getArgs() {
        return this.args;
    }

    public String getDefaultMessage() {
        return this.defaultMessage;
    }
}
