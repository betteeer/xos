package com.inossem.sco.common.core.web.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AjaxResult<T> {
    Integer code;
    String msg;
    T data;
    Map<String, Object> extMap = null;
    private static final long serialVersionUID = 1L;

    public AjaxResult() {
    }

    public Integer getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }

    public T getData() {
        return this.data;
    }

    @JsonInclude(Include.NON_NULL)
    public Map<String, Object> getExtMap() {
        return this.extMap;
    }

    public AjaxResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public AjaxResult(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static AjaxResult success() {
        return success("SUCCESS");
    }

    public static AjaxResult success(String msg) {
        return new AjaxResult(200, msg, (Object)null);
    }

    public static <T> AjaxResult success(T data) {
        return new AjaxResult(200, "SUCCESS", data);
    }

    public static AjaxResult error() {
        return error("ERROR");
    }

    public static AjaxResult error(String msg) {
        return new AjaxResult(500, msg, (Object)null);
    }

    public static <T> AjaxResult error(T data) {
        return new AjaxResult(500, "ERROR", data);
    }

    public AjaxResult<T> withData(T data) {
        this.data = data;
        return this;
    }

    public static AjaxResult error(int code, String msg) {
        return new AjaxResult(code, msg, (Object)null);
    }

    public boolean isSuccess() {
        return Objects.equals(200, this.code);
    }

    public AjaxResult put(String key, Object value) {
        if (null == this.extMap) {
            this.extMap = new HashMap();
        }

        this.extMap.put(key, value);
        return this;
    }
}
