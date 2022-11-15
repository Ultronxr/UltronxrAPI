package cn.ultronxr.ultronxrapi.config;

import cn.ultronxr.ultronxrapi.interceptor.AuthInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Ultronxr
 * @date 2022/11/02 21:23
 */
@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;


    /**
     * 注册拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns()
                .order(1);
        // 一个*：只匹配字符，不匹配路径（/）；两个**：匹配字符和路径（/）
        // 这里可以配置拦截器启用的 path 的顺序，在有多个拦截器存在时，任一拦截器返回 false 都会使后续的请求方法不再执行
    }

    /**
     * 设置跨域访问
     * 注意：addInterceptors() 拦截器的存在会使 addCorsMappings() 跨域配置失效！
     *       此时不应该使用 CorsMapping，而是使用 CorsFilter。
     * {@link https://blog.csdn.net/weixin_33958585/article/details/88678712}
     * {@link https://blog.csdn.net/huangyaa729/article/details/103893660}
     */
    //@Override
    //public void addCorsMappings(CorsRegistry registry) {
    //    registry.addMapping("/**")
    //            .allowedOriginPatterns("*")
    //            .allowedHeaders("*")
    //            .allowedMethods("*")
    //            .allowCredentials(true)
    //            .maxAge(3600);
    //}

}
