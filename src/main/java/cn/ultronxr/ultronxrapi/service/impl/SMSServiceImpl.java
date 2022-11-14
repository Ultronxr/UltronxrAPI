package cn.ultronxr.ultronxrapi.service.impl;

import cn.ultronxr.ultronxrapi.bean.ResBundle;
import cn.ultronxr.ultronxrapi.bean.Response;
import cn.ultronxr.ultronxrapi.bean.ResponseCode;
import cn.ultronxr.ultronxrapi.service.SMSService;
import cn.ultronxr.ultronxrapi.util.SmsUtils;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author Ultronxr
 * @date 2022/11/02 21:41
 */
@Slf4j
@Service
public class SMSServiceImpl implements SMSService {

    private static final String SMS_SIGN = ResBundle.TENCENT_CLOUD.getString("sms.sign");

    private static final String SMS_TEMPLATE_ID_OBJECT_MONITOR = ResBundle.TENCENT_CLOUD.getString("sms.template.id.objectMonitor");
    private static final Integer SMS_TEMPLATE_PARAM_LENGTH_OBJECT_MONITOR = Integer.valueOf(ResBundle.TENCENT_CLOUD.getString("sms.template.param.length.objectMonitor"));


    @Override
    public Response sendSMS(String phoneNumber, String templateId, String[] templateParamSet) {
        // 特判电话号码为空
        if(StringUtils.isEmpty(phoneNumber)) {
            return new Response(ResponseCode.PARAM_ERROR, "Parameter 'phoneNumber' is not allowed to be empty.");
        }
        // 特判短信模板ID为空
        if(StringUtils.isEmpty(templateId)) {
            return new Response(ResponseCode.PARAM_ERROR, "Parameter 'templateId' is not allowed to be empty.");
        }

        ResponseCode responseCode;
        String msg = null;

        try {
            SendSmsResponse sendSmsResponse = SmsUtils.tencentSms(SMS_SIGN, new String[]{phoneNumber}, templateId, templateParamSet);
            log.debug(SmsUtils.responseToString(sendSmsResponse));

            // 对调用发送短信接口的返回结果进行分类处理
            // 1 返回结果SendSmsResponse不为空，分类讨论
            if(sendSmsResponse.getSendStatusSet() != null && sendSmsResponse.getSendStatusSet().length > 0) {
                String sendStatusSetCode = sendSmsResponse.getSendStatusSet()[0].getCode(),
                        sendStatusSetMessage = sendSmsResponse.getSendStatusSet()[0].getMessage();
                // 1.1 发送成功
                if(sendStatusSetCode.equals("Ok") && sendStatusSetMessage.equals("send success")) {
                    responseCode = ResponseCode.SUCCESS;
                    msg = "send success";
                } else {
                    // 1.2 发送失败
                    responseCode = ResponseCode.FAIL;
                    msg = sendSmsResponse.getSendStatusSet()[0].getMessage();
                }
            } else {
                // 2 返回结果SendSmsResponse为空，只有一种情况：短信模板ID为空（不会达成这种情况，在上面特判掉了）
                responseCode = ResponseCode.PARAM_ERROR;
                msg = "Parameter 'templateId' is not allowed to be empty.";
            }
        } catch (TencentCloudSDKException e) {
            responseCode = ResponseCode.SERVER_ERROR;
            throw new RuntimeException(e);
        }
        return new Response(responseCode, msg);
    }

    @Override
    public Response easySendSMSofObjectMonitor(String phoneNumber, String[] templateParamSet) {
        // 特判短信模板参数值数量
        if(templateParamSet == null || templateParamSet.length != SMS_TEMPLATE_PARAM_LENGTH_OBJECT_MONITOR) {
            return new Response(ResponseCode.PARAM_ERROR, "Length of parameter 'templateParamSet' is error.");
        }
        return this.sendSMS(phoneNumber, SMS_TEMPLATE_ID_OBJECT_MONITOR, templateParamSet);
    }

}
