package com.lzw.easyhttp.net;

/**
 * Author: lzw
 * Date: 2018/10/23
 * Description: This is ICallBack
 */

public interface ICallBack {
    void onSuccess(String result);

    void onFail(String e);
}
