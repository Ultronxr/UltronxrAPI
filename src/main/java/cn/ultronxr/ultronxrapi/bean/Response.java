package cn.ultronxr.ultronxrapi.bean;

import lombok.Data;

/**
 * @author Ultronxr
 * @date 2022/11/02 21:55
 */
@Data
public class Response {

    private String code;
    private String msg;


    public Response() {
    }

    public Response(ResponseCode responseCode) {
        this.code = responseCode.getCode();
        this.msg = "";
    }

    public Response(ResponseCode responseCode, String msg) {
        this.code = responseCode.getCode();
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "{" +
                "\"code\":\"" + code + "\"" +
                ",\"msg\":\"" + msg + "\"" +
                "}";
    }

}
