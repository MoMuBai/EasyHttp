package com.lzw.easyhttp.net;

import java.util.Map;

/**
 * Author: lzw
 * Date: 2018/10/23
 * Description: This is IHttpProcessor
 */

public interface IHttpProcessor {
    void post(String url, Map<String, Object> params, ICallBack callBack);

    void get(String url, ICallBack callBack);
}
