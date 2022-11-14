package cn.ultronxr.ultronxrapi.service;

import cn.ultronxr.ultronxrapi.bean.Response;

/**
 * @author Ultronxr
 * @date 2022/11/02 21:41
 */
public interface SMSService {

    Response sendSMS(String phoneNumber, String templateId, String[] templateParamSet);

    Response easySendSMSofObjectMonitor(String phoneNumber, String[] templateParamSet);

}
