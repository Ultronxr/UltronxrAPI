package cn.ultronxr.ultronxrapi.auth.bean;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Ultronxr
 * @date 2022/11/15 23:19
 * @description 签名串的分解内容，从客户端请求中解析而来
 */
@Data
public class StringToSignContent {

    /** 【必填】HTTP 请求方法，全部大写，比如 POST */
    private String httpMethod;

    /**
     * 【可选】请求头中的Content-MD5的值
     * 可为空，只有在请求存在Body且Body为非Form形式时才计算Content-MD5头<br/>
     * 计算方式为：{@code Base64.encodeBase64(MD5(bodyStream.getBytes("UTF-8")));}
     */
    private String contentMD5;

    /** 【必填】请求头中的时间戳（x-ca-timestamp） */
    private String timestamp;

    /** 【必填】请求头中的随机数（x-ca-nonce） */
    private String nonce;

    /**
     * 【必填】请求路径+参数列表<br/>
     * 包含Path，Query和Form中的所有参数，其具体形式为：<br/>
     *     Path + "?" + Key1 + "=" + Value1 + "&" + Key2 + "=" + Value2 + ... "&" + KeyN + "=" + ValueN<br/>
     * - Query和Form参数对的Key按照字典排序后使用上面的方式拼接；<br/>
     * - Query和Form参数为空时，则直接使用Path，不需要添加 ? ；<br/>
     * - 参数的Value为空时只保留Key参与签名，等号不需要再加入签名；<br/>
     * - Query和Form存在数组参数时（key相同，value不同的参数） ，取第一个Value参与签名计算。<br/>
     */
    private String pathAndParameters;


    public StringToSignContent() {
    }

    public StringToSignContent(String httpMethod, String contentMD5, String timestamp, String nonce, String pathAndParameters) {
        this.httpMethod = httpMethod;
        this.contentMD5 = contentMD5;
        this.timestamp = timestamp;
        this.nonce = nonce;
        this.pathAndParameters = pathAndParameters;
    }

    public String assemble() {
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
