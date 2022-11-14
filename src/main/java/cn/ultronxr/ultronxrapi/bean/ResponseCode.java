package cn.ultronxr.ultronxrapi.bean;

/**
 * @author Ultronxr
 * @date 2022/11/03 17:58
 */
public enum ResponseCode {

    // 未授权。
    UNAUTHORIZED("UNAUTHORIZED"),
    // 失败。这个失败指的是用户未达到最终目的，无法获得预期结果（不是指没有调用到服务器的API），且服务器无法预测发生的错误类别（否则直接返回下面的各种类别的错误）。
    FAIL("FAIL"),
    // 成功。这个成功指的是用户达到了最终目的，成功获得了预期结果（不是指成功调用到了服务器的API而不管后续结果）。
    SUCCESS("SUCCESS"),
    // 包含在HTTP状态码（HTTP Status Code）内的错误类别，404、415、500等等。
    HTTP_STATUS_CODE_ERROR("HTTP_STATUS_CODE_ERROR"),
    // 传入API的参数错误。
    PARAM_ERROR("PARAM_ERROR"),
    // 服务器内部错误。例如：代码执行抛出异常。
    SERVER_ERROR("SERVER_ERROR"),
    // 调用云API错误。例如：服务器调用了腾讯云/阿里云提供的API，调用失败。
    CLOUD_API_ERROR("CLOUD_API_ERROR"),
    // 其他未知错误。
    UNKNOWN_ERROR("UNKNOWN_ERROR"),
    ;


    private String code;


    ResponseCode() {
    }

    ResponseCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "{" +
                "\"code\":\"" + code + "\"" +
                "}";
    }

}
