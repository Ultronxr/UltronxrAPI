package cn.ultronxr.ultronxrapi.auth.service;

import cn.hutool.core.lang.Pair;
import cn.hutool.crypto.digest.MD5;
import cn.ultronxr.ultronxrapi.auth.bean.EncryptionAlgorithm;
import cn.ultronxr.ultronxrapi.auth.bean.KeyPair;
import cn.ultronxr.ultronxrapi.auth.bean.Signature;
import cn.ultronxr.ultronxrapi.auth.bean.StringToSignContent;
import cn.ultronxr.ultronxrapi.bean.ResBundle;
import cn.ultronxr.ultronxrapi.util.EncryptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

/**
 * @author Ultronxr
 * @date 2022/11/14 14:56
 */
@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    /** 密钥对 KeyPair 和 Secret 的集合 */
    public static final ArrayList<Pair<String, String>> AUTH_KEY_LIST;

    /** 已请求过的随机数池，如果接收到的请求中的随机数 nonce 在池中存在，则拒绝服务 */
    private static final List<String> NONCE_POOL = Collections.synchronizedList(new LinkedList<>());
    /** 随机数池容量限制 */
    private static final int NONCE_POOL_SIZE_LIMIT = 100;

    /** 客户端时间戳与服务端时间戳之间可容忍的最大时间间隔（毫秒） */
    private static final long TIMESTAMP_GAP_LIMIT = 2*60*1000;


    static {
        AUTH_KEY_LIST = new ArrayList<>(5);
        AUTH_KEY_LIST.add(new Pair<>(ResBundle.AUTH.getString("key"), ResBundle.AUTH.getString("secret")));
    }


    @Override
    public KeyPair getKeyPair(String key) {
        if(StringUtils.isBlank(key)) {
            return KeyPair.justKey(key);
        }
        for (Pair<String, String> pair : AUTH_KEY_LIST) {
            if(pair.getKey().equals(key)) {
                return new KeyPair(key, pair.getValue());
            }
        }
        return KeyPair.empty();
    }

    @Override
    public String checkSignature(HttpServletRequest request) {
        String key = request.getHeader("x-ca-key"),
                signatureAlgorithm = request.getHeader("x-ca-signature-algorithm"),
                clientSignature = request.getHeader("x-ca-signature");
        EncryptionAlgorithm algorithm = EncryptionAlgorithm.HmacSHA256;

        // ===== 0. 特判：接收到的请求中是否包含 Key 、合法的 EncryptionAlgorithm 、以及客户端生成的 Signature ； =====
        if(StringUtils.isEmpty(key)) {
            return "Key为空。";
        }
        if(StringUtils.isNotEmpty(signatureAlgorithm)) {
            algorithm = EncryptionAlgorithm.support(signatureAlgorithm);
            if(null == algorithm) {
                return "不支持的签名加密算法。";
            }
        }
        if(StringUtils.isEmpty(clientSignature)) {
            return "客户端签名为空。";
        }

        // ===== 1. 特判：请求的时间戳（与服务端时间戳相差时间间隔是否合理）、随机数是否合法（该随机数在一定时间内是否已被请求过）； =====
        String timestamp = request.getHeader("x-ca-timestamp"),
                nonce = request.getHeader("x-ca-nonce");
        if(!checkTimestamp(timestamp) || !checkNonce(nonce)) {
            return "时间戳或随机因子不合法。";
        }

        // ===== 2. 从请求中读取 Key ，查询对应的 Secret 并装箱成 KeyPair ； =====
        KeyPair keyPair = getKeyPair(key);
        if(!keyPair.isLegal()) {
            return "未授权的Key。";
        }

        // ===== 3. 从请求中提取关键数据，得到一个用来签名的字符串； =====
        StringToSignContent stringToSignContent = getStringToSignContent(request);
        if(!stringToSignContent.isLegal()) {
            return "签名串字段缺失。";
        }

        // ===== 4. 使用加密算法和 KeyPair 中的 Secret 对签名串进行加密处理，得到签名； =====
        Signature serverSignature = generateServerSignature(algorithm, keyPair, stringToSignContent);
        if(!serverSignature.isLegal()) {
            return "服务端签名生成失败。";
        }

        // ===== 5. 从接收到的请求中读取客户端签名，对比服务器端签名和客户端签名的一致性。 =====
        if(!compareSignatures(clientSignature, serverSignature)) {
            return "客户端签名校验失败。";
        }

        // 签名校验成功
        return null;
    }

    @Override
    public StringToSignContent getStringToSignContent(HttpServletRequest request) {
        StringToSignContent stringToSignContent = new StringToSignContent();
        stringToSignContent.setHttpMethod(request.getMethod());
        stringToSignContent.setTimestamp(request.getHeader("x-ca-timestamp"));
        stringToSignContent.setNonce(request.getHeader("x-ca-nonce"));

        // TODO 这里读取了之后后面controller里面request body为空，会抛异常
        String content;
        try {
            content = new String(request.getInputStream().readAllBytes());
        } catch (IOException e) {
            e.printStackTrace();
            return stringToSignContent;
        }
        stringToSignContent.setContentMD5(MD5.create().digestHex(content));

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
        stringToSignContent.setPathAndParameters(request.getServletPath() + parameters);

        return stringToSignContent;
    }

    @Override
    public Signature generateServerSignature(EncryptionAlgorithm algorithm, KeyPair keyPair, StringToSignContent stringToSignContent) {
        Signature signature = new Signature(algorithm, keyPair, stringToSignContent);
        String signatureStr = EncryptionUtils.base64Encode(
                EncryptionUtils.HmacSHA256Encrypt(keyPair, signature.getStringToSign())
        );
        signature.setSignature(signatureStr);
        return signature;
    }

    @Override
    public boolean compareSignatures(String clientSignature, Signature serverSignature) {
        return compareSignatures(clientSignature, serverSignature.getSignature());
    }

    @Override
    public boolean compareSignatures(String clientSignature, String serverSignature) {
        return clientSignature.equals(serverSignature);
    }

    @Override
    public boolean checkTimestamp(String timestamp) {
        Long serverTS = Calendar.getInstance().getTimeInMillis(),
                clientTS = Long.parseLong(timestamp);
        return Math.abs(serverTS - clientTS)  <= TIMESTAMP_GAP_LIMIT;
    }

    @Override
    public boolean checkNonce(String nonce) {
        if (NONCE_POOL.size() > NONCE_POOL_SIZE_LIMIT) {
            NONCE_POOL.remove(0);
        }
        if(NONCE_POOL.contains(nonce)) {
            return false;
        }
        NONCE_POOL.add(nonce);
        return true;
    }

}
