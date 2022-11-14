package cn.ultronxr.ultronxrapi.util;

import cn.ultronxr.ultronxrapi.bean.ResBundle;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * @author Ultronxr
 * @date 2022/11/02 20:36
 */
@Slf4j
public class SmsUtils {

    /**
     * 腾讯云发送SMS短信服务
     *
     * @param smsSign
     *          短信签名，一个字符串，标识发送短信的个人/组织，向腾讯云短信服务申请，且需要审核通过才能使用
     *          如果传入的字符串为null或为空，则读取配置文件中的默认短信签名
     *
     * @param phoneNumberSet
     *          接收短信的手机号字符串数组
     *          如果传入的数组为null或为空，则读取配置文件中的默认手机号
     *          请注意手机号格式（需要携带加号和区号，再接手机号），例：["+8612345678901", "+8612345678902"]
     *
     * @param templateId
     *          短信模板ID，一个数字字符串，从腾讯云短信服务申请分配，且需要审核通过才能使用
     *          如果传入的字符串为null或为空，则直接return一个空的SendSmsResponse对象，停止发送短信
     *
     * @param templateParamSet
     *          短信模板中的参数值数组
     *          如果短信模板中存在{1}、{2}...等参数，就需要对应的参数值分别填入其中，才能组合成完整的短信
     *
     * @return
     *          {@code SendSmsResponse}腾讯云短信服务的发送短信通用类，记录这次短信发送过程的各种信息
     *          可以使用{@code SendSmsResponse.toJsonString()}方法输出完整内容
     *          如果传参smsTemplateId为null或为空，将会return一个空的{@code SendSmsResponse}对象
     *
     * @throws TencentCloudSDKException
     *          腾讯云SDK执行异常
     */
    public static SendSmsResponse tencentSms(String smsSign, String[] phoneNumberSet, String templateId, String[] templateParamSet)
            throws TencentCloudSDKException {
        if(StringUtils.isEmpty(smsSign)){
            smsSign = ResBundle.TENCENT_CLOUD.getString("sms.sign");
        }
        if(StringUtils.isEmpty(templateId)){
            return new SendSmsResponse();
        }
        if(Objects.isNull(phoneNumberSet) || phoneNumberSet.length == 0){
            return new SendSmsResponse();
        }

        Credential tencentApiCredential = new Credential(
                ResBundle.TENCENT_CLOUD.getString("secret.id"),
                ResBundle.TENCENT_CLOUD.getString("secret.key")
        );

        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint("sms.ap-shanghai.tencentcloudapi.com");

        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);

        SmsClient smsClient = new SmsClient(tencentApiCredential, "ap-guangzhou", clientProfile);
        SendSmsRequest req = new SendSmsRequest();
        req.setSmsSdkAppId(ResBundle.TENCENT_CLOUD.getString("app.id"));
        req.setSignName(smsSign);
        req.setPhoneNumberSet(phoneNumberSet);
        req.setTemplateId(templateId);
        req.setTemplateParamSet(templateParamSet);

        return smsClient.SendSms(req);
    }

    /**
     * 把发送短信后返回的{@code SendSmsResponse}对象转成JsonString的结果字符串
     *
     * @param sendSmsResponse 发送短信后返回的{@code SendSmsResponse}对象
     * @return json字符串
     */
    public static String responseToString(SendSmsResponse sendSmsResponse) {
        return SendSmsResponse.toJsonString(sendSmsResponse);
    }

}