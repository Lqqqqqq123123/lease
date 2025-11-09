package com.atguigu.lease.common.Exception;

import lombok.Data;

public enum BussinessExceptionEnum {
    ;

    private Integer code;
    private String message;

    private BussinessExceptionEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
