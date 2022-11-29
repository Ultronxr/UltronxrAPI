package cn.ultronxr.ultronxrapi.interceptor;

import cn.ultronxr.ultronxrapi.bean.Response;
import cn.ultronxr.ultronxrapi.auth.service.AuthService;
import cn.ultronxr.ultronxrapi.bean.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

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
        String checkResult = authService.checkSignature(request);
        if(StringUtils.isNotEmpty(checkResult)) {
            return doResponse(response, new Response(ResponseCode.UNAUTHORIZED, checkResult));
        }
        return true;
    }

    private boolean doResponse(HttpServletResponse hsResponse, Response response) throws Exception {
        hsResponse.setContentType("application/json;charset=utf-8");
        hsResponse.setContentLength(response.toString().getBytes(StandardCharsets.UTF_8).length);
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
