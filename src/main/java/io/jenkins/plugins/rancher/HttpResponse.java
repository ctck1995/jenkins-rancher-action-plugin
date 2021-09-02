package io.jenkins.plugins.rancher;

import java.io.Serializable;

/**
 * Created by ck on 2021/9/1.
 * <p/>
 */
public class HttpResponse implements Serializable {
    private static final long serialVersionUID = 2727880031155557242L;

    private int code;
    private String data;

    public HttpResponse() {
    }

    public HttpResponse(int code, String data) {
        this.code = code;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "code=" + code +
                ", data='" + data + '\'' +
                '}';
    }
}
