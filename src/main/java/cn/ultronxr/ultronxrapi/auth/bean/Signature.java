package cn.ultronxr.ultronxrapi.auth.bean;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Ultronxr
 * @date 2022/11/15 23:03
 * @description 摘要签名<br/>
 *              完整流程参考：<a href="https://help.aliyun.com/document_detail/29475.htm">阿里云摘要签名认证方式原理说明</a>
 */
@Data
public class Signature {

    /**
     * 签名使用的加密算法<br/>
     * 由客户端指定（x-ca-signature-algorithm），若空则默认使用 HmacSHA256
     */
    private EncryptionAlgorithm algorithm;

    /**
     * 密钥对
     * 客户端请求 key （x-ca-key），服务端由这个 key 查询出 secret ，装箱成 KeyPair
     */
    private KeyPair keyPair;

    /**
     * 签名串
     * 由 {@link StringToSignContent} 拼接而来
     */
    private String stringToSign;

    /** 最终生成的服务端签名 */
    private String signature;


    public Signature() {
    }

    public Signature(EncryptionAlgorithm algorithm, KeyPair keyPair, StringToSignContent stringToSignContent) {
        this.algorithm = algorithm;
        this.keyPair = keyPair;
        this.stringToSign = stringToSignContent.assemble();
    }

    public boolean isLegal() {
        return StringUtils.isNotBlank(algorithm.getAlgorithm())
                && keyPair.isLegal()
                && StringUtils.isNotBlank(stringToSign)
                && StringUtils.isNotBlank(signature);
    }

}
