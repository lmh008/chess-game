package com.github.controller.dispatch.convert;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Title
 * Author jirenhe@wanshifu.com
 * Time 2017/7/16.
 * Version v1.0
 */
public class ComplicatedConvert implements ParamsConvert {

    private static final Logger logger = LoggerFactory.getLogger(ComplicatedConvert.class);

    @Override
    public Object convertParams(DataInfo dataInfo, String parameterName, Class<?> parameterClass, ParamsConvertChain convertChain) {
        Object paramValue = null;
        try {
            if (dataInfo.getDataToJsonObject() != null) {
                Object fieldInData;
                if ((fieldInData = dataInfo.getDataToJsonObject().get(parameterName)) != null) {
                    paramValue = JSON.parseObject(JSON.toJSONString(fieldInData), parameterClass); //尝试转型
                } else {
                    paramValue = JSON.parseObject(dataInfo.getDataToJsonStr(), parameterClass); //尝试转型
                }
            }
        } catch (Exception e) {
            logger.info("param json parse fail!");
        }
        return paramValue == null ? convertChain.convertParams(dataInfo, parameterName, parameterClass, convertChain) : paramValue;
    }
}
