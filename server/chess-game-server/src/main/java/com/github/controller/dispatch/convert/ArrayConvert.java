package com.github.controller.dispatch.convert;

import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.TypeMismatchException;

import java.util.Collection;

/**
 * Title
 * Author jirenhe@wanshifu.com
 * Time 2017/7/16.
 * Version v1.0
 */
public class ArrayConvert implements ParamsConvert {

    private TypeConverter typeConverter = new SimpleTypeConverter();

    @Override
    public Object convertParams(DataInfo dataInfo, String parameterName, Class<?> parameterClass, ParamsConvertChain convertChain) {
        Object paramValue = null;
        try {
            if (parameterClass.isArray() || Collection.class.isAssignableFrom(parameterClass)) {//如果是数组或者集合
                Object fieldInData;
                if (dataInfo.getDataToJsonArray() != null) {
                    paramValue = typeConverter.convertIfNecessary(dataInfo.getDataToJsonArray(), parameterClass); //尝试转型
                } else if (dataInfo.getDataToJsonObject() != null && (fieldInData = dataInfo.getDataToJsonObject().get(parameterName)) != null) {
                    paramValue = typeConverter.convertIfNecessary(fieldInData, parameterClass); //尝试转型
                }
            }
        } catch (TypeMismatchException e) {
        }
        return paramValue == null ? convertChain.convertParams(dataInfo, parameterName, parameterClass, convertChain) : paramValue;
    }
}
