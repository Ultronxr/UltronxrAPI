package cn.ultronxr.ultronxrapi;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Ultronxr
 * @date 2022/11/02 20:45
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        cn.ultronxr.ultronxrapi.bean.ResBundle.class,
        cn.ultronxr.ultronxrapi.util.SmsUtils.class,
})
public class SMSTests {

    @Test
    public void sendSMS() {
        log.info("111");
    }

}
