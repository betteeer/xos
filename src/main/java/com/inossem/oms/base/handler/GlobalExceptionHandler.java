package com.inossem.oms.base.handler;

import com.inossem.sco.common.core.exception.ServiceException;
import com.inossem.sco.common.core.web.domain.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.List;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 权限校验异常
     *//*
    @ExceptionHandler(AccessDeniedException.class)
    public AjaxResult handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',权限校验失败'{}'", requestURI, e.getMessage());
        return AjaxResult.error("Insufficient permissions");
    }*/

    /**
     * 请求方式不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public AjaxResult handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e,
                                                      HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',不支持'{}'请求", requestURI, e.getMethod());
        return AjaxResult.error(e.getMessage());
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(ServiceException.class)
    public AjaxResult handleServiceException(ServiceException e, HttpServletRequest request) {
        log.error(e.getMessage(), e);
        Integer code = e.getCode();
        return AjaxResult.error(e.getMessage());
    }

    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public AjaxResult handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生未知异常.", requestURI, e);
        return AjaxResult.error(e.getMessage());
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public AjaxResult handleException(Exception e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生系统异常.", requestURI, e);
        return AjaxResult.error("Server is being maintained");
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(BindException.class)
    public AjaxResult handleBindException(BindException e) {
        log.error(e.getMessage(), e);
        String message = e.getAllErrors().get(0).getDefaultMessage();
        return AjaxResult.error(message);
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public AjaxResult handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        String name = e.getParameter().getExecutable().getName();
        log.info("=========接口调用{}=========",name);
        BindingResult rs = e.getBindingResult();
        StringBuilder resultMsg =new StringBuilder();
        if (rs.hasErrors()) {
            List<FieldError> fieldErrors = rs.getFieldErrors();
            fieldErrors.forEach(fieldError -> {
                resultMsg.append(fieldError.getDefaultMessage()).append(",");
                //这里可以放入Map，最后return map;
                log.error("error field is : {} ,message is : {}", fieldError.getField(), fieldError.getDefaultMessage());
            });
        }
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        return AjaxResult.error(resultMsg.deleteCharAt(resultMsg.length() - 1).toString());
    }

    /**
     * 处理@NotBlank校验的数据
     * @param e
     * @return
     */
    @ExceptionHandler(value = {ConstraintViolationException.class})
    public AjaxResult ConstraintViolationException(ConstraintViolationException e) {
        String excMsg = e.getMessage();
        String name = excMsg.substring(0,excMsg.indexOf("."));
        String msg = excMsg.substring(excMsg.indexOf(":")+1);
        String fieldName = excMsg.substring(excMsg.indexOf(".") + 1, excMsg.indexOf(":"));
        log.info("=========接口调用{}=========",name);
        log.error("error field is : {} ,message is : {}", fieldName, msg);
        return AjaxResult.error(msg);
    }

}
