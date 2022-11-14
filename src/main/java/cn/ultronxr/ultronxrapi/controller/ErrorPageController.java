package cn.ultronxr.ultronxrapi.controller;

import cn.ultronxr.ultronxrapi.bean.Response;
import cn.ultronxr.ultronxrapi.bean.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Ultronxr
 * @date 2022/11/04 22:28
 */
@Slf4j
@Controller
@RequestMapping("/errorPageController")
public class ErrorPageController {


    @RequestMapping(value = "error_{errorCode}", produces = "application/json;charset=utf-8")
    @ResponseBody
    public Response error(@PathVariable Integer errorCode) {
        log.warn("web 请求错误，错误代码：{}", errorCode);
        ResponseCode responseCode;
        HttpStatus httpStatus = HttpStatus.resolve(errorCode);
        switch (errorCode) {
            default: responseCode = ResponseCode.HTTP_STATUS_CODE_ERROR; break;
        }
        Response response = new Response(responseCode, httpStatusToString(httpStatus));
        log.warn("web 请求错误，response：{}", response.toString());
        return response;
    }

    private String httpStatusToString(HttpStatus httpStatus) {
        return httpStatus.value() + " | " + httpStatus.series().name() + " | " + httpStatus.getReasonPhrase();
    }

}
