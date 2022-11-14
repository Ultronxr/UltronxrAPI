package cn.ultronxr.ultronxrapi.service.impl;

import cn.hutool.core.lang.Pair;
import cn.hutool.crypto.digest.HMac;
import cn.hutool.crypto.digest.HmacAlgorithm;
import cn.hutool.crypto.digest.MD5;
import cn.ultronxr.ultronxrapi.bean.ResBundle;
import cn.ultronxr.ultronxrapi.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author Ultronxr
 * @date 2022/11/14 14:56
 */
@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    /** 支持的加密算法 */
    private static final ArrayList<String> SUPPORTED_ENCRYPTION_ALGORITHM;

    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    /** 密钥对 Key 和 Secret 的集合 */
    public static final ArrayList<Pair<String, String>> AUTH_KEY_LIST;

    /**
     * 已请求过的随机数元素池
     * 这是个队列，已请求过的随机数元素从队尾插入，从队首删除
     * 每个元素的 Key 是其生成的时间戳、Value 是随机数值
     * 此外，依据 NONCE_EXPIRES_MILLISECONDS 对其中的元素进行遍历，删除已过期的随机数元素
     */
    private static final Queue<Pair<Long, String>> NONCE_POOL = new ArrayBlockingQueue<>(10);
    public static final Long NONCE_EXPIRES_MILLISECONDS = 10L*60*1000;


    static {
        SUPPORTED_ENCRYPTION_ALGORITHM = new ArrayList<>(1);
        SUPPORTED_ENCRYPTION_ALGORITHM.add("HmacSHA256");

        AUTH_KEY_LIST = new ArrayList<>(5);
        AUTH_KEY_LIST.add(new Pair<>(ResBundle.AUTH.getString("key"), ResBundle.AUTH.getString("secret")));
    }


    @Override
    public boolean whetherToSupportTheEncryptionAlgorithm(String algorithm) {
        return SUPPORTED_ENCRYPTION_ALGORITHM.contains(algorithm);
    }

    @Override
    public String getSecret(String key) {
        for (Pair<String, String> pair : AUTH_KEY_LIST) {
            if(pair.getKey().equals(key)) {
                return pair.getValue();
            }
        }
        return null;
    }

    @Override
    public String maintainNoncePool(int flag, Pair<Long, String> nonce) {
        long timestampNow = Calendar.getInstance().getTimeInMillis();
        switch (flag) {
            case 0 : {
                // 遍历随机数池，删除已过期的元素
                Iterator<Pair<Long, String>> itor = NONCE_POOL.iterator();
                while (itor.hasNext()) {
                    Pair<Long, String> pair = itor.next();
                    long timeGap = Math.abs(pair.getKey() - timestampNow);
                    if(timeGap >= NONCE_EXPIRES_MILLISECONDS) {
                        itor.remove();
                    }
                }
            }
            case 1 : {
                // 从队尾插入元素
                // 队列已满：自动删除队首元素
                if(NONCE_POOL.size() == 10) {
                    NONCE_POOL.poll();
                }
                NONCE_POOL.offer(nonce);
            }
            default: break;
        }
        return null;
    }

    @Override
    public String generateStringToSign(HttpServletRequest request) {
        String httpMethod = request.getMethod(),
                content = "",
                contentMD5 = MD5.create().digestHex16(content),
                timestamp = request.getHeader("timestamp"),
                nonce = request.getHeader("nonce"),
                pathAndParameters = request.getPathTranslated() + request.getParameterMap().toString();
        String stringToSign = httpMethod + "\n" +
                contentMD5 + "\n" +
                timestamp + "\n" +
                nonce + "\n" +
                pathAndParameters + "\n";
        return stringToSign;
    }

    @Override
    public String generateSign(String secret, String stringToSign) {
        return base64Decode(
                HmacSHA256Encrypt(secret, stringToSign)
        );
    }

    public static String base64Encode(String stringToEncode) {
        return Arrays.toString(
                Base64.getEncoder().encode(stringToEncode.getBytes(UTF_8))
        );
    }

    public static String base64Decode(String stringToDecode) {
        return Arrays.toString(
                Base64.getDecoder().decode(stringToDecode.getBytes(UTF_8))
        );
    }

    public static String HmacSHA256Encrypt(String secret, String stringToEncrypt) {
        return new HMac(HmacAlgorithm.HmacSHA256, secret.getBytes(UTF_8)).digestHex(stringToEncrypt);
    }

}
