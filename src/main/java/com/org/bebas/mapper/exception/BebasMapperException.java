package com.org.bebas.mapper.exception;

import com.org.bebas.exception.BaseRuntimeException;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author WuHao
 * @date 2022/5/22 20:11
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BebasMapperException extends BaseRuntimeException {

    private static final long serialVersionUID = 1L;

    private int code;

    private String message;

    public BebasMapperException() {
    }

    public BebasMapperException(String message) {
        this.message = message;
        super.setMessage(message);
    }

    public BebasMapperException(String message, int code) {
        this.message = message;
        this.code = code;
        super.setMessage(message);
    }


}
