package cn.ultronxr.ultronxrapi.util;

import cn.hutool.crypto.digest.HMac;
import cn.hutool.crypto.digest.HmacAlgorithm;
import cn.ultronxr.ultronxrapi.auth.bean.KeyPair;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author Ultronxr
 * @date 2022/11/17 00:18
 */
public class EncryptionUtils {

    private static final Charset UTF_8 = StandardCharsets.UTF_8;


    public static String base64Encode(String stringToEncode) {
        return new String(Base64.getEncoder().encode(stringToEncode.getBytes(UTF_8)));
    }

    public static String base64Decode(String stringToDecode) {
        return new String(Base64.getDecoder().decode(stringToDecode.getBytes(UTF_8)));
    }

    public static String HmacSHA256Encrypt(KeyPair keyPair, String stringToEncrypt) {
        return new HMac(HmacAlgorithm.HmacSHA256, keyPair.getSecret().getBytes(UTF_8)).digestHex(stringToEncrypt, UTF_8.name());
    }

}
