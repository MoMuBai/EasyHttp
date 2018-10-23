package com.lzw.easyhttp.net;

import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Author: lzw
 * Date: 2018/10/23
 * Description: This is HttpCallBack
 */

public abstract class HttpCallBack<Result> implements ICallBack {

    @Override
    public void onSuccess(String result) {
        Type cls = asyncData();
        Result t = (Result) JSONObject.parseObject(result, cls);
        onSuccess(t);
    }

    /**
     * 获取参数类型
     *
     * @return
     */
    public Type asyncData() {
        Type type = this.getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            Type[] resultType = ((ParameterizedType) type).getActualTypeArguments();
            return resultType[0];
        } else {
            return null;
        }
    }

    @Override
    public void onFail(String e) {
        onError(e);
    }


    /**
     * 对外暴露的方法返回指定的Data
     *
     * @param result
     */
    public abstract void onSuccess(Result result);

    /**
     * @param e
     */
    public abstract void onError(String e);
}
