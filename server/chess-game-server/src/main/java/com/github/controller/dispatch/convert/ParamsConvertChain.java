package com.github.controller.dispatch.convert;

import java.util.ArrayList;
import java.util.List;

/**
 * Title
 * Author jirenhe@wanshifu.com
 * Time 2017/7/16.
 * Version v1.0
 */
public class ParamsConvertChain implements ParamsConvert {

    private List<ParamsConvert> converts = new ArrayList<ParamsConvert>();
    private int index = -1;

    public ParamsConvertChain addParamsConvert(ParamsConvert paramsConvert) {
        this.converts.add(paramsConvert);
        return this;
    }

    public void reset() {
        this.index = -1;
    }

    @Override
    public Object convertParams(DataInfo dataInfo, String paramName, Class<?> paramType, ParamsConvertChain convertChain) {
        index++;
        if (index == converts.size()) return null;
        return converts.get(index).convertParams(dataInfo, paramName, paramType, convertChain);
    }
}
