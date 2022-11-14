package cn.ultronxr.ultronxrapi.config;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * @author Ultronxr
 * @date 2022/11/04 22:27
 */
@Component
public class ErrorPageConfig implements ErrorPageRegistrar {

    private static final String CONTROLLER_PREFIX = "/errorPageController/error_";


    @Override
    public void registerErrorPages(ErrorPageRegistry registry) {
        ErrorPage err400 = new ErrorPage(HttpStatus.BAD_REQUEST, CONTROLLER_PREFIX + "400"),
                err401 = new ErrorPage(HttpStatus.UNAUTHORIZED, CONTROLLER_PREFIX + "401"),
                err404 = new ErrorPage(HttpStatus.NOT_FOUND, CONTROLLER_PREFIX + "404"),
                err405 = new ErrorPage(HttpStatus.METHOD_NOT_ALLOWED, CONTROLLER_PREFIX + "405"),
                err415 = new ErrorPage(HttpStatus.UNSUPPORTED_MEDIA_TYPE, CONTROLLER_PREFIX + "415"),
                err500 = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, CONTROLLER_PREFIX + "500");
        registry.addErrorPages(err400, err401, err404, err405, err415, err500);
    }

}
