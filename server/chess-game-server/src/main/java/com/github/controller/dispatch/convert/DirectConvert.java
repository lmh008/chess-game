package com.github.controller.dispatch.convert;

import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.TypeMismatchException;

/**
 * Title
 * Author jirenhe@wanshifu.com
 * Time 2017/7/16.
 * Version v1.0
 */
public class DirectConvert implements ParamsConvert {

    private TypeConverter typeConverter = new SimpleTypeConverter();

    @Override
    public Object convertParams(DataInfo dataInfo, String parameterName, Class<?> paramType, ParamsConvertChain convertChain) {
        Object paramValue = null;
        try {
            paramValue = typeConverter.convertIfNecessary(dataInfo.getData(), paramType); //尝试直接转型
        } catch (TypeMismatchException e) {
        }
        if (paramValue == null) {
            Object fieldInData;
            try {
                if (dataInfo.getDataToJsonObject() != null && (fieldInData = dataInfo.getDataToJsonObject().get(parameterName)) != null)
                {//尝试从json拿属性转型
                    paramValue = typeConverter.convertIfNecessary(fieldInData, paramType);
                }
            } catch (TypeMismatchException e) {
            }
        }

        return paramValue == null ? convertChain.convertParams(dataInfo, parameterName, paramType, convertChain) : paramValue;
    }
}
