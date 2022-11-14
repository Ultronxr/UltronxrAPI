package cn.ultronxr.ultronxrapi.bean;

import lombok.Data;

import java.util.ResourceBundle;

/**
 * @author Ultronxr
 * @date 2022/11/02 20:37
 */
@Data
public class ResBundle {

    public static final ResourceBundle AUTH = ResourceBundle.getBundle("authConfig");

    public static final ResourceBundle TENCENT_CLOUD = ResourceBundle.getBundle("tencentCloudConfig");

    public static final ResourceBundle ALI_CLOUD = ResourceBundle.getBundle("aliCloudConfig");

}
