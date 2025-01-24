package org.example.filetool.TooUtil;

import java.io.Serializable;

/**
 * Created by PaulPeng on 2019/6/25.
 */
public class Response<T> implements Serializable {
    public final static String RES_OK="200";
    public final static String RES_PARAMETER="400";
    public final static String RES_NOT_FOUND="404";
    public final static String RES_ERROR="500";

    public final static String RES_OK_MSG="成功";
    public final static String RES_PARAMETER_MSG="请求参数异常";
    public final static String RES_NOT_FOUND_MSG="未找到资源";
    public final static String RES_ERROR_MSG="服务器返回异常";

    /** 通用请求头信息 */
    String resultCode;
    /** 请求主体信息 */
    String resultMsg;
    T resultData;

    public Response(){

    }

    public Response(String code, String resultMsg){
        this.resultCode=code;
        this.resultMsg=resultMsg;
    }

    public Response(String code, String resultMsg, T resultData){
        this.resultCode=code;
        this.resultMsg=resultMsg;
        this.resultData=resultData;
    }


    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public T getResultData() {
        return resultData;
    }

    public void setResultData(T resultData) {
        this.resultData = resultData;
    }
}
