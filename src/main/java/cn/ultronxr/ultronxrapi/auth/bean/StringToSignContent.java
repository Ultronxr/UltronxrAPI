package cn.ultronxr.ultronxrapi.auth.bean;

import cn.hutool.crypto.digest.MD5;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author Ultronxr
 * @date 2022/11/15 23:19
 *
 * 签名串的分解内容
 */
@Data
public class StringToSignContent {

    private String httpMethod;
    private String contentMD5;
    private String timestamp;
    private String nonce;
    private String pathAndParameters;


    public StringToSignContent() {
    }

    public String parseRequest(final HttpServletRequest request) throws Exception {
        this.httpMethod = request.getMethod();

        // TODO 这里读取了之后后面controller里面request body为空，会抛异常
        String content = new String(request.getInputStream().readAllBytes());
        this.contentMD5 = MD5.create().digestHex(content);

        this.timestamp = request.getHeader("x-ca-timestamp");

        this.nonce = request.getHeader("x-ca-nonce");

        // 把参数map按照Key字典升序排序后拼接成字符串："?" + Key1 + "=" + Value1 + "&" + Key2 + "=" + Value2 + ... "&" + KeyN + "=" + ValueN
        // 参数的Value为空时只保留Key参与签名，等号不需要再加入签名
        // 存在数组参数时（Key相同，Value不同的参数），取第一个Value参与签名计算。
        String parameters = "";
        Map<String, String[]> parameterMap = request.getParameterMap();
        if(parameterMap.size() > 0) {
            StringBuilder sb = new StringBuilder();
            parameterMap.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEachOrdered(entry -> {
                        if(entry.getValue().length > 0) {
                            sb.append(entry.getKey()).append("=").append(entry.getValue()[0]).append("&");
                        } else {
                            sb.append(entry.getKey()).append("&");
                        }
                    });
            // 删去最后一个多余的 & 符号
            parameters = "?" + sb.deleteCharAt(sb.length() - 1);
        }
        this.pathAndParameters = request.getServletPath() + parameters;

        return assembleStringToSign();
    }

    public String assembleStringToSign() {
        return this.httpMethod + "\n" +
                this.contentMD5 + "\n" +
                this.timestamp + "\n" +
                this.nonce + "\n" +
                this.pathAndParameters + "\n";
    }

    public boolean isLegal() {
        return StringUtils.isNotEmpty(httpMethod)
                && StringUtils.isNotEmpty(contentMD5)
                && StringUtils.isNotEmpty(timestamp)
                && StringUtils.isNotEmpty(nonce)
                && StringUtils.isNotEmpty(pathAndParameters);
    }

}
