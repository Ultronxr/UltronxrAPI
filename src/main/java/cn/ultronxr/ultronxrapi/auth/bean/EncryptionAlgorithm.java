package cn.ultronxr.ultronxrapi.auth.bean;

import java.util.Arrays;

/**
 * @author Ultronxr
 * @date 2022/11/16 00:33
 * @description 摘要签名认证服务支持的加密算法
 */
public enum EncryptionAlgorithm {


    HmacSHA256("HmacSHA256");


    private String algorithm;

    EncryptionAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * 签名认证服务是否支持某种加密算法（检查此枚举中是否存在某种加密算法）<br/>
     * 如果支持，会返回对应名称的枚举；否则返回 null
     *
     * @param algorithm 待检查的加密算法
     * @return 支持 - 返回对应名称的 {@code EncryptionAlgorithm} 枚举；不支持 - 返回 null
     */
    public static EncryptionAlgorithm support(String algorithm) {
        return Arrays.stream(EncryptionAlgorithm.values())
                .filter(al -> al.getAlgorithm().equals(algorithm))
                .findFirst()
                .orElse(null);
    }

}
