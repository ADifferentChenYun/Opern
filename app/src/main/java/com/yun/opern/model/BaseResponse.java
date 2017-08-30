package com.yun.opern.model;

/**
 * Created by Yun on 2017/7/3 0003.
 */
public class BaseResponse<T> {
    public static final int RETURN_SUCCESS = 0;
    public static final int RETURN_FAIL = -1;

    protected int code = RETURN_SUCCESS;
    protected String message = "";
    protected T data;

    public boolean isSuccess(){
        return code == RETURN_SUCCESS;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
