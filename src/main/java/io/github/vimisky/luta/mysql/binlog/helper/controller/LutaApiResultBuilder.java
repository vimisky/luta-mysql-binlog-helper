package io.github.vimisky.luta.mysql.binlog.helper.controller;

public class LutaApiResultBuilder<T> {

    public static LutaApiResult ok() {
        LutaApiResult  lutaApiResult = new LutaApiResult();
        lutaApiResult.setCode(LutaApiResultCode.SUCCESS);
        lutaApiResult.setSuccess(true);
        lutaApiResult.setMessage("成功");
        return lutaApiResult;
    }
    public static LutaApiResult ok(Object data) {
        LutaApiResult  lutaApiResult = new LutaApiResult();
        lutaApiResult.setCode(LutaApiResultCode.SUCCESS);
        lutaApiResult.setSuccess(true);
        lutaApiResult.setMessage("成功");
        lutaApiResult.setData(data);
        return lutaApiResult;
    }
    public static LutaApiResult error() {
        LutaApiResult  lutaApiResult = new LutaApiResult();
        lutaApiResult.setCode(LutaApiResultCode.ERROR);
        lutaApiResult.setSuccess(false);
        lutaApiResult.setMessage("失败");
        return lutaApiResult;
    }
    public static LutaApiResult error(Object data) {
        LutaApiResult  lutaApiResult = new LutaApiResult();
        lutaApiResult.setCode(LutaApiResultCode.ERROR);
        lutaApiResult.setSuccess(false);
        lutaApiResult.setMessage("失败");
        lutaApiResult.setData(data);
        return lutaApiResult;
    }
}
