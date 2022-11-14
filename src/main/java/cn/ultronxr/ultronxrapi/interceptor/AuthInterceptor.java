package cn.ultronxr.ultronxrapi.interceptor;

import cn.ultronxr.ultronxrapi.bean.Response;
import cn.ultronxr.ultronxrapi.bean.ResponseCode;
import cn.ultronxr.ultronxrapi.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Ultronxr
 * @date 2022/11/02 21:20
 *
 * 鉴权拦截器
 */
@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private AuthService authService;


    /**
     * 在进入controller前执行
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String key =  request.getHeader("x-ca-key"),
                signatureAlgorithm = request.getHeader("x-ca-signature-algorithm"),
                signature = request.getHeader("x-ca-signature");

        if(StringUtils.isEmpty(signature)) {
            return doResponse(response, new Response(ResponseCode.UNAUTHORIZED, "请求签名为空。"));
        }
        if(authService.whetherToSupportTheEncryptionAlgorithm(signatureAlgorithm)) {
            return doResponse(response, new Response(ResponseCode.UNAUTHORIZED, "不支持这种签名加密算法。"));
        }
        String secret = authService.getSecret(key);
        String stringToSign = authService.generateStringToSign(request);
        String sign = authService.generateSign(secret, stringToSign);
        // 左边为真则直接返回true，左边为假则返回右边
        return sign.equals(signature) || doResponse(response, new Response(ResponseCode.UNAUTHORIZED, "签名校验失败。"));
    }

    private boolean doResponse(HttpServletResponse hsResponse, Response response) throws Exception {
        hsResponse.setContentType("application/json;charset=UTF-8");
        hsResponse.setContentLength(response.toString().length());
        hsResponse.getWriter().write(response.toString());
        hsResponse.getWriter().flush();
        hsResponse.getWriter().close();
        return false;
    }

    /**
     * 在controller执行结束后执行
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    /**
     * 在view视图渲染完成后执行
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }

}
