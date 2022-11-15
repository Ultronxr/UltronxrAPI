package cn.ultronxr.ultronxrapi.auth.service;

import cn.hutool.core.lang.Pair;
import cn.ultronxr.ultronxrapi.auth.bean.EncryptionAlgorithm;
import cn.ultronxr.ultronxrapi.auth.bean.KeyPair;
import cn.ultronxr.ultronxrapi.bean.ResBundle;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author Ultronxr
 * @date 2022/11/14 14:56
 */
@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    /** 密钥对 KeyPair 和 Secret 的集合 */
    public static final ArrayList<Pair<String, String>> AUTH_KEY_LIST;

    /**
     * 已请求过的随机数元素池
     * 这是个队列，已请求过的随机数元素从队尾插入，从队首删除
     * 每个元素的 KeyPair 是其生成的时间戳、Value 是随机数值
     * 此外，依据 NONCE_EXPIRES_MILLISECONDS 对其中的元素进行遍历，删除已过期的随机数元素
     */
    private static final Queue<Pair<Long, String>> NONCE_POOL = new ArrayBlockingQueue<>(10);
    public static final Long NONCE_EXPIRES_MILLISECONDS = 10L*60*1000;


    static {
        AUTH_KEY_LIST = new ArrayList<>(5);
        AUTH_KEY_LIST.add(new Pair<>(ResBundle.AUTH.getString("key"), ResBundle.AUTH.getString("secret")));
    }


    @Override
    public KeyPair getKeyPair(String key) {
        if(StringUtils.isBlank(key)) {
            return null;
        }
        for (Pair<String, String> pair : AUTH_KEY_LIST) {
            if(pair.getKey().equals(key)) {
                return new KeyPair(key, pair.getValue());
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

}
