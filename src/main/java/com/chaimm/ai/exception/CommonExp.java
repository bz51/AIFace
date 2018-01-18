package com.chaimm.ai.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author 大闲人柴毛毛
 * @date 2017/12/31 上午10:07
 * @description
 */
public class CommonExp extends Exception {

    public CommonExp(String message) {
        super(message);
    }

}
