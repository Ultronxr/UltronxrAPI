package cn.ultronxr.ultronxrapi.auth.service;

import cn.ultronxr.ultronxrapi.auth.bean.EncryptionAlgorithm;
import cn.ultronxr.ultronxrapi.auth.bean.KeyPair;
import cn.ultronxr.ultronxrapi.auth.bean.Signature;
import cn.ultronxr.ultronxrapi.auth.bean.StringToSignContent;

import javax.servlet.http.HttpServletRequest;

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
     * 校验客户端请求的签名是否合法，包含如下几步：<br/>
     * 0. 特判：接收到的请求中是否包含 Key 、合法的 EncryptionAlgorithm 、以及客户端生成的 Signature ；<br/>
     * 1. 特判：请求的时间戳（与服务端时间戳相差时间间隔是否合理）、随机数是否合法（该随机数在一定时间内是否已被请求过）；<br/>
     * 2. 从请求中读取 Key ，查询对应的 Secret 并装箱成 KeyPair ；<br/>
     * 3. 从请求中提取关键数据，得到一个用来签名的字符串；<br/>
     * 4. 使用加密算法和 KeyPair 中的 Secret 对签名串进行加密处理，得到签名；<br/>
     * 5. 从接收到的请求中读取客户端签名，对比服务器端签名和客户端签名的一致性。<br/>
     * <br/>
     * 以上任意一步出错都会直接判定签名校验失败，并返回失败原因；<br/>
     * 全部通过则校验成功，返回 null 。<br/>
     *
     * @param request 客户端请求
     * @return 非 null 且非空的 {@code String} 字符串 - 签名校验不通过，返回的内容为不通过原因；<br/>
     *         null - 签名校验通过。
     */
    String checkSignature(HttpServletRequest request);

    /**
     * 从接收到的请求中解析出签名串的每一项内容
     *
     * @param request 客户端请求
     * @return 封装的签名串每一项内容 {@link StringToSignContent}
     */
    StringToSignContent getStringToSignContent(HttpServletRequest request);

    /**
     * 生成服务端签名
     *
     * @param algorithm           签名加密算法
     * @param keyPair             加密密钥对
     * @param stringToSignContent 封装的签名串每一项内容 {@link StringToSignContent}
     * @return 封装的服务端生成的签名 {@link Signature}
     */
    Signature generateServerSignature(EncryptionAlgorithm algorithm, KeyPair keyPair, StringToSignContent stringToSignContent);

    /**
     * 校验客户端签名与服务端签名是否一致
     *
     * @param clientSig 客户端请求的签名
     * @param serverSig 服务端生成的签名
     * @return true - 两者签名一致；<br/>
     *         false - 两者签名不一致
     */
    boolean compareSignatures(String clientSig, Signature serverSig);

    /**
     * 校验客户端签名与服务端签名是否一致
     *
     * @param clientSig 客户端请求的签名
     * @param serverSig 服务端生成的签名
     * @return true - 两者签名一致；<br/>
     *         false - 两者签名不一致
     */
    boolean compareSignatures(String clientSig, String serverSig);

    /**
     * 检查客户端请求的时间戳 timestamp 服务端时间戳相差时间间隔是否合理<br/>
     * 如果时间间隔相差过大，则拒绝服务
     *
     * @param timestamp 客户端请求的时间戳
     * @return true - 时间间隔在合理范围内，检查通过；<br/>
     *         false - 时间间隔超出限制范围，检查不通过。
     */
    boolean checkTimestamp(String timestamp);

    /**
     * 检查该随机数在一定时间内是否已被请求过<br/>
     * 即检查客户端请求的随机数 nonce 是否在服务端“已请求过的随机数池”中存在<br/>
     * 如果请求的随机数已存在，则拒绝服务
     *
     * @param nonce 客户端请求的随机数
     * @return true - 随机数池中不存在请求的 nonce ，检查通过；<br/>
     *         false - 随机数池中存在请求的 nonce ，检查不通过。
     */
    boolean checkNonce(String nonce);

}
