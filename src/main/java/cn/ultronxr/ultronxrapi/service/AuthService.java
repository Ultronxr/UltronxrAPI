package cn.ultronxr.ultronxrapi.service;

import cn.hutool.core.lang.Pair;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Ultronxr
 * @date 2022/11/14 14:56
 * <br/>
 * 摘要签名认证服务<br/>
 * 完整流程参考：<a href="https://help.aliyun.com/document_detail/29475.htm">阿里云摘要签名认证方式原理说明</a>
 */
public interface AuthService {

    /**
     * 检查签名认证服务是否支持某种加密算法
     *
     * @param algorithm 待检查的加密算法
     * @return true - 支持; false - 不支持
     */
    boolean whetherToSupportTheEncryptionAlgorithm(String algorithm);

    /**
     * 根据提供的密钥 Key 查询出对应的 Secret <br/>
     * 相当于阿里云中的 APP Key 和 APP Secret
     *
     * @param key     密钥Key（密钥的ID，明文传输）
     * @return secret 密钥Secret（实际用于加密/解密的密钥内容，不可泄漏）
     */
    String getSecret(String key);

    /**
     * 维护 已请求过的随机数池
     *
     * @param flag  请求动作：
     *              0 - 维护（全部遍历一遍，删除已过期的随机数）
     *              1 - 新增（push）
     * @param nonce 随机数
     * @return
     */
    String maintainNoncePool(int flag, Pair<Long, String> nonce);

    String generateStringToSign(HttpServletRequest request);

    String generateSign(String secret, String stringToSign);

}
