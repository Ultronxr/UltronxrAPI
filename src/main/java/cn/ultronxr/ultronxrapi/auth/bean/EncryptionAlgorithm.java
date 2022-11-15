package cn.ultronxr.ultronxrapi.auth.bean;

import java.util.Arrays;

/**
 * @author Ultronxr
 * @date 2022/11/16 00:33
 *
 * 摘要签名认证服务支持的加密算法
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
     * 签名认证服务是否支持某种加密算法（检查此枚举中是否存在某种加密算法）
     *
     * @param algorithm 待检查的加密算法
     * @return true - 支持; false - 不支持
     */
    public static boolean support(String algorithm) {
        return Arrays.stream(EncryptionAlgorithm.values()).anyMatch(al -> al.getAlgorithm().equals(algorithm));
    }

}
