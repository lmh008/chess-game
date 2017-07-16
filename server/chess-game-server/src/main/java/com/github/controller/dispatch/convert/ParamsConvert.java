package com.github.controller.dispatch.convert;

/**
 * Title
 * Author jirenhe@wanshifu.com
 * Time 2017/7/16.
 * Version v1.0
 */
public interface ParamsConvert {

    Object convertParams(DataInfo dataInfo, String parameterName, Class<?> parameterType, ParamsConvertChain convertChain);
}
