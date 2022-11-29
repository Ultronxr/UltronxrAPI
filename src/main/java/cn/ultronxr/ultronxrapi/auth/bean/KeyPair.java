package cn.ultronxr.ultronxrapi.auth.bean;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Ultronxr
 * @date 2022/11/15 23:17
 * @description 密钥对，包含一个 key 和一个 secret （相当于阿里云中的 APP Key 和 APP Secret）
 */
@Data
public class KeyPair {

    /** 密钥的ID，可以明文传输 */
    private String key;
    /** 实际用于加密/解密的密钥内容，不可泄漏 */
    private String secret;

    /** 空密钥对单例 */
    private static final KeyPair EMPTY_KEYPAIR = new KeyPair();


    public KeyPair() {
    }

    public KeyPair(String key, String secret) {
        this.key = key;
        this.secret = secret;
    }

    public boolean isLegal() {
        return StringUtils.isNotBlank(key)
                && StringUtils.isNotBlank(secret);
    }

    public static KeyPair justKey(String key) {
        return new KeyPair(key, null);
    }

    public static KeyPair empty() {
        return KeyPair.EMPTY_KEYPAIR;
    }

}
