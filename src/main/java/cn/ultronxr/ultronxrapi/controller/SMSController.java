package cn.ultronxr.ultronxrapi.controller;

import cn.ultronxr.ultronxrapi.bean.Response;
import cn.ultronxr.ultronxrapi.bean.ResponseCode;
import cn.ultronxr.ultronxrapi.service.SMSService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author Ultronxr
 * @date 2022/11/02 21:00
 */
@Slf4j
@Controller
@RequestMapping(value = "/api/sms",
        method = {RequestMethod.GET, RequestMethod.POST},
        consumes = "application/json;charset=utf-8",
        produces = "application/json;charset=utf-8")
public class SMSController {

    @Autowired
    private SMSService smsService;


    @RequestMapping("sendSMS")
    @ResponseBody
    public String sendSMS(@RequestBody Map<String, Object> request) {
        String phoneNumber = request.get("phoneNumber").toString(),
                templateId = request.get("templateId").toString();
        String[] templateParamSet = ((ArrayList<String>) request.get("templateParamSet")).toArray(new String[0]);
        //Response response = smsService.sendSMS(phoneNumber, templateId, templateParamSet);
        Response response = new Response(ResponseCode.SUCCESS, "测试成功");
        log.info(response.toString());
        return response.toString();
    }

    @RequestMapping("easySendSMSofObjectMonitor")
    @ResponseBody
    public String easySMSofObjectMonitor(@RequestBody Map<String, Object> request) {
        String phoneNumber = request.get("phoneNumber").toString();
        String[] templateParamSet = ((ArrayList<String>) request.get("templateParamSet")).toArray(new String[0]);
        //Response response = smsService.easySendSMSofObjectMonitor(phoneNumber, templateParamSet);
        Response response = new Response(ResponseCode.SUCCESS, "测试成功");
        log.info(response.toString());
        return response.toString();
    }

}
