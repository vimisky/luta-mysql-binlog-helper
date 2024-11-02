package io.github.vimisky.luta.mysql.binlog.helper.controller;

public class LutaApiResult<T> implements LutaApiResultCode {

    private Integer code; //返回状态码
    private String message; //返回消息
    private Boolean success; //请求是否成功
    private T data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
