package cn.ultronxr.ultronxrapi.auth.bean;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Ultronxr
 * @date 2022/11/15 23:03
 *
 * 摘要签名
 * 完整流程参考：<a href="https://help.aliyun.com/document_detail/29475.htm">阿里云摘要签名认证方式原理说明</a>
 */
@Data
public class Signature extends StringToSignContent {

    /** 签名使用的加密算法 */
    private EncryptionAlgorithm algorithm;

    /** 密钥对 */
    private KeyPair keyPair;

    /** 签名串 */
    private String stringToSign;

    /** 签名 */
    private String signature;


    public Signature() {
    }

    public Signature(final KeyPair keyPair, final HttpServletRequest request) {
        this.keyPair = keyPair;
        try {
            this.parseRequest(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String parseRequest(final HttpServletRequest request) throws Exception {
        this.stringToSign = super.parseRequest(request);
        this.signature = generateSignature();
        return this.signature;
    }

    public String generateSignature() throws Exception {
        if(!keyPair.isLegal()) {
            throw new Exception("密钥对不可用，无法生成签名！");
        }
        return this.keyPair.base64Encode(
                this.keyPair.HmacSHA256Encrypt(this.stringToSign)
        );
    }

    public boolean isLegal() {
        return StringUtils.isNotBlank(algorithm.getAlgorithm())
                && keyPair.isLegal()
                && super.isLegal()
                && StringUtils.isNotBlank(stringToSign)
                && StringUtils.isNotBlank(signature);
    }

}
