package cn.ultronxr.ultronxrapi.auth.bean;

import cn.hutool.crypto.digest.HMac;
import cn.hutool.crypto.digest.HmacAlgorithm;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author Ultronxr
 * @date 2022/11/15 23:17
 *
 * 密钥对，包含一个 key 和一个 secret （相当于阿里云中的 APP Key 和 APP Secret）
 */
@Data
public class KeyPair {

    /** 密钥的ID，可以明文传输 */
    private String key;
    /** 实际用于加密/解密的密钥内容，不可泄漏 */
    private String secret;

    private static final Charset UTF_8 = StandardCharsets.UTF_8;


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

    public String base64Encode(String stringToEncode) {
        return new String(Base64.getEncoder().encode(stringToEncode.getBytes(UTF_8)));
    }

    public String base64Decode(String stringToDecode) {
        return new String(Base64.getDecoder().decode(stringToDecode.getBytes(UTF_8)));
    }

    public String HmacSHA256Encrypt(String stringToEncrypt) {
        return new HMac(HmacAlgorithm.HmacSHA256, this.secret.getBytes(UTF_8)).digestHex(stringToEncrypt, "UTF-8");
    }

}
