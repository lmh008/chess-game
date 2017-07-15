package com.github.controller;

import com.alibaba.fastjson.*;
import jdk.nashorn.internal.parser.JSONParser;
import jdk.nashorn.internal.scripts.JS;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.TypeConverter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Title
 * Author jirenhe@wanshifu.com
 * Time 2017/7/15.
 * Version v1.0
 */
@Configuration
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
            Object handlerBean = this.applicationContext.getBean(requestHandlerMapping.getRegisterBeanType());
            Object[] params = this.autoWiredParams(requestHandlerMapping, session, data);
            requestHandlerMapping.getRegisterMethod().invoke(handlerBean, params);
        } else {
            throw new IllegalAccessException("can not find mapping");
        }
    }

    private Object[] autoWiredParams(RequestHandlerMapping requestHandlerMapping, WebSocketSession session, Object data) {
        Parameter[] paramTypes = requestHandlerMapping.getRegisterMethod().getParameters();
        LocalVariableTableParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
        String[] paramNames = parameterNameDiscoverer.getParameterNames(requestHandlerMapping.getRegisterMethod());
        Object[] params = null;
        //TODO 这里代码很丑陋，待重构。 考虑使用策略模式：分基本数据类型、数组或集合、复杂对象，三种策略
        if (paramTypes != null && paramTypes.length > 0) {
            params = new Object[paramTypes.length];
            Parameter parameter = null;
            boolean dataIsPrimitive = data.getClass().isPrimitive() || data instanceof String;
            JSONArray dataToJsonArray = null;
            JSONObject dataToJsonObject = null;
            String dataToJsonStr = null;
            if (data instanceof JSONArray) {
                dataToJsonArray = (JSONArray) data;
            }
            if (data instanceof JSONObject) {
                dataToJsonObject = (JSONObject) data;
                dataToJsonStr = JSON.toJSONString(data);

            }
            Class<?> parameterClass = null;
            String parameterName = null;
            Object sourceValue = null;
            TypeConverter typeConverter = new SimpleTypeConverter();
            for (int i = 0; i < paramTypes.length; i++) {
                parameter = paramTypes[i];
                parameterClass = parameter.getType();
                parameterName = paramNames[i];
                params[i] = null;
                if (parameterClass == WebSocketSession.class) {
                    params[i] = session;
                } else if (parameterClass.isPrimitive() || parameterClass == String.class) { //基本参数
                    if (dataIsPrimitive) {
                        params[i] = typeConverter.convertIfNecessary(data, parameterClass); //尝试直接转型
                    }
                    if (params[i] == null) {
                        if (dataToJsonObject != null && (sourceValue = dataToJsonObject.get(parameterName)) != null) {//尝试从json拿属性转型
                            params[i] = typeConverter.convertIfNecessary(sourceValue, parameterClass);
                        }
                    }
                } else if (parameterClass.isArray() || parameterClass.isAssignableFrom(Collection.class)) {//如果是数组或者集合
                    if (dataToJsonArray != null) {
                        params[i] = typeConverter.convertIfNecessary(dataToJsonArray, parameterClass); //尝试转型
                    } else if (dataToJsonObject != null && (sourceValue = dataToJsonObject.get(parameterName)) != null) {
                        params[i] = typeConverter.convertIfNecessary(sourceValue, parameterClass); //尝试转型
                    }
                } else { //否则是复杂对象
                    if (dataToJsonObject != null) {
                        if ((sourceValue = dataToJsonObject.get(parameterName)) != null) {
                            params[i] = JSON.parseObject(JSON.toJSONString(sourceValue), parameterClass); //尝试转型
                        } else {
                            params[i] = JSON.parseObject(dataToJsonStr, parameterClass); //尝试转型
                        }
                    }
                }
            }
            return params;
        }
        return null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
