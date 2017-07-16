package com.github.controller.dispatch;

import java.lang.reflect.Method;

/**
 * Title
 * Author jirenhe@wanshifu.com
 * Time 2017/7/15.
 * Version v1.0
 */
public class RequestHandlerMapping {

    private Class<?> registerBeanType;

    private Method registerMethod;

    public RequestHandlerMapping(Class<?> registerBeanType, Method registerMethod) {
        this.registerBeanType = registerBeanType;
        this.registerMethod = registerMethod;
    }

    public Class<?> getRegisterBeanType() {
        return registerBeanType;
    }

    public Method getRegisterMethod() {
        return registerMethod;
    }
}
