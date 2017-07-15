package com.github.controller;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.socket.WebSocketSession;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * Title
 * Author jirenhe@wanshifu.com
 * Time 2017/7/15.
 * Version v1.0
 */
public class WebSocketRequestDispatch implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private Map<String, Map<String, RequestHandlerMapping>> registerMapping;

    public void initHandlerMapping() {
        registerMapping = new HashMap<>();
        Map<String, Object> map = this.applicationContext.getBeansWithAnnotation(WebSocketMapping.class);
        String basePath;
        WebSocketMapping webSocketMapping;
        Class<?> handlerBeanClass;
        Method[] methods;
        RequestHandlerMapping requestHandlerMapping = null;
        Map<String, RequestHandlerMapping> handlerMapping = null;
        for (Object handlerBean : map.values()) {
            handlerBeanClass = handlerBean.getClass();
            methods = handlerBeanClass.getDeclaredMethods();
            webSocketMapping = handlerBeanClass.getDeclaredAnnotation(WebSocketMapping.class);
            basePath = webSocketMapping.value();
            handlerMapping = new HashMap<>();
            for (Method method : methods) {
                if ((webSocketMapping = method.getDeclaredAnnotation(WebSocketMapping.class)) != null) {
                    handlerMapping.put(webSocketMapping.value(), new RequestHandlerMapping(handlerBeanClass, method));
                }
            }
            this.registerMapping.put(basePath, handlerMapping);
        }
    }

    public void doDispatch(WebSocketSession session, String topic, String tag, Object data) throws InvocationTargetException, IllegalAccessException {
        Map<String, RequestHandlerMapping> handlerMapping = this.registerMapping.get(topic);
        if (handlerMapping != null) {
            RequestHandlerMapping requestHandlerMapping = handlerMapping.get(tag);
            Object handlerBean = this.applicationContext.getBean(requestHandlerMapping.getClass());
            Parameter[] paramTypes = requestHandlerMapping.getRegisterMethod().getParameters();
            Object[] params = null;
            if (paramTypes != null && paramTypes.length > 0) {
                params = new Object[paramTypes.length];
                Parameter parameter = null;
                for (int i = 0; i < paramTypes.length; i++) {
                    parameter = paramTypes[i];
                    if (parameter.getType() == WebSocketSession.class) {
                        params[i] = session;
                    } else {
                        params[i] = null;
                    }
                }
            }
            requestHandlerMapping.getRegisterMethod().invoke(handlerBean, params);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
