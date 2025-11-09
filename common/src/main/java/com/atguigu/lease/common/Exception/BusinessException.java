package com.atguigu.lease.common.Exception;

import lombok.Data;
import org.jetbrains.annotations.TestOnly;

@Data
public class BusinessException extends RuntimeException {

    private Integer code;
    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(BussinessExceptionEnum bussinessExceptionEnum) {
        super(bussinessExceptionEnum.getMessage());
        this.code = bussinessExceptionEnum.getCode();
    }
}

