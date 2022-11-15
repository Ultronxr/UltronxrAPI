package cn.ultronxr.ultronxrapi.auth.service;

import cn.hutool.core.lang.Pair;
import cn.ultronxr.ultronxrapi.auth.bean.KeyPair;

/**
 * @author Ultronxr
 * @date 2022/11/14 14:56
 *
 * 摘要签名认证服务
 * 完整流程参考：<a href="https://help.aliyun.com/document_detail/29475.htm">阿里云摘要签名认证方式原理说明</a>
 */
public interface AuthService {

    /**
     * 根据提供的密钥 Key 查询出对应的 Secret ，并包装成密钥对 {@link KeyPair} <br/>
     *
     * @param key     密钥 key
     * @return {@link KeyPair} 包含密钥 key 和密钥 secret 的完整密钥对
     */
    KeyPair getKeyPair(String key);

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

}
