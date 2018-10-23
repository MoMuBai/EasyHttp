package com.lzw.easyhttp.net;

import java.util.Map;

/**
 * Author: lzw
 * Date: 2018/10/23
 * Description: This is HttpHelper
 */

public class HttpHelper implements IHttpProcessor {

    private static IHttpProcessor mIHttpProcessor = null;

    private static HttpHelper httpHelper = null;


    public static void init(IHttpProcessor iHttpProcessor) {
        mIHttpProcessor = iHttpProcessor;
    }

    private HttpHelper() {
    }


    public static HttpHelper obtain() {
        synchronized (HttpHelper.class) {
            if (null == httpHelper) {
                httpHelper = new HttpHelper();
            }
            return httpHelper;
        }
    }


    @Override
    public void post(String url, Map<String, Object> params, ICallBack callBack) {
        mIHttpProcessor.post(url, params, callBack);
    }

    @Override
    public void get(String url, ICallBack callBack) {
        mIHttpProcessor.get(url, callBack);
    }
}
