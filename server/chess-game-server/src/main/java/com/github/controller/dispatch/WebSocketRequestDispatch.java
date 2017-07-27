package com.github.controller.dispatch;

import com.github.controller.dispatch.convert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.util.Assert;
import org.springframework.web.socket.WebSocketSession;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Title
 * Author jirenhe@wanshifu.com
 * Time 2017/7/15.
 * Version v1.0
 * 配置根据topic、tag分发请求
 * 基于使用责任链模式为请求处理方法注入参数
 */
@Configuration
public class WebSocketRequestDispatch implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private Map<String, Map<String, RequestHandlerMapping>> registerMapping;

    private ParamsConvertChain paramsConvertChain;

    private static final Logger logger = LoggerFactory.getLogger(WebSocketRequestDispatch.class);

    public void init() {
        this.initRegisterMapping();
        this.initParamsConvertChain();
    }

    /**
     * 初始化请求映射
     */
    private void initRegisterMapping() {
        registerMapping = new ConcurrentHashMap<>();
        Map<String, Object> map = this.applicationContext.getBeansWithAnnotation(WebSocketMapping.class);
        String basePath;
        WebSocketMapping webSocketMapping;
        Class<?> handlerBeanClass;
        Method[] methods;
        Map<String, RequestHandlerMapping> topicMappingMap = null;
        for (Object handlerBean : map.values()) {
            handlerBeanClass = handlerBean.getClass();
            methods = handlerBeanClass.getDeclaredMethods();
            webSocketMapping = handlerBeanClass.getDeclaredAnnotation(WebSocketMapping.class);
            basePath = webSocketMapping.value();
            topicMappingMap = new HashMap<>();
            for (Method method : methods) {
                if ((webSocketMapping = method.getDeclaredAnnotation(WebSocketMapping.class)) != null) {
                    topicMappingMap.put(webSocketMapping.value(), new RequestHandlerMapping(handlerBeanClass, method));
                }
            }
            this.registerMapping.put(basePath, topicMappingMap);
        }
    }

    /**
     * 初始化参数类型转换
     */
    private void initParamsConvertChain() {
        paramsConvertChain = new ParamsConvertChain();
        paramsConvertChain.addParamsConvert(new WebSocketSessionConvert()).addParamsConvert(new DirectConvert()).
                addParamsConvert(new ArrayConvert()).addParamsConvert(new ComplicatedConvert());
    }

    /**
     * 分发请求
     *
     * @param session
     * @param topic
     * @param tag
     * @param data
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public void doDispatch(WebSocketSession session, String topic, String tag, Object data) throws Exception {
        Map<String, RequestHandlerMapping> topicMappingMap = this.registerMapping.get(topic);
        if (topicMappingMap != null) {
            RequestHandlerMapping tagHandlerMapping = topicMappingMap.get(tag);
            if (tagHandlerMapping != null) {
                try {
                    Object handlerBean = this.applicationContext.getBean(tagHandlerMapping.getRegisterBeanType());
                    Assert.notNull(handlerBean, "type:" + tagHandlerMapping.getRegisterBeanType());
                    Object[] params = this.autoWiredParams(tagHandlerMapping.getRegisterMethod(), session, data);
                    tagHandlerMapping.getRegisterMethod().invoke(handlerBean, params);
                } catch (Throwable e) {
                    throw new Exception("invoke error, tag: " + tag + "for topic:" + topic + " params : " + data, e);
                }
            } else {
                throw new IllegalAccessException("can not find mapping tag: " + tag + "for topic:" + topic);
            }
        } else {
            throw new IllegalAccessException("can not find mapping topic");
        }
    }

    /**
     * 自动为请求方法注入参数
     *
     * @param targetMethod
     * @param session
     * @param data
     * @return
     */
    private Object[] autoWiredParams(Method targetMethod, WebSocketSession session, Object data) {
        Parameter[] paramTypes = targetMethod.getParameters();
        if (paramTypes != null && paramTypes.length > 0) {
            LocalVariableTableParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
            String[] paramNames = parameterNameDiscoverer.getParameterNames(targetMethod);
            Object[] params = new Object[paramTypes.length];
            Class<?> parameterClass = null;
            String parameterName = null;
            DataInfo dataInfo = new DataInfo(data, session, com.github.ApplicationContext.allOnlinePlayer.get(session.getId()));
            for (int i = 0; i < paramTypes.length; i++) {
                parameterClass = paramTypes[i].getType();
                parameterName = paramNames[i];
                this.paramsConvertChain.reset();
                params[i] = this.paramsConvertChain.convertParams(dataInfo, parameterName, parameterClass, this.paramsConvertChain);
                if (params[i] == null) {
                    logger.warn("param : " + parameterName + " can not matching!");
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
