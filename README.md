# UltronxrAPI

一些通用API，独立出来写，后续用到直接请求调用即可。

API服务采用摘要签名认证，完整流程参见[阿里云摘要签名认证方式原理说明](https://help.aliyun.com/document_detail/29475.htm)。

## .gitignore 中排除的敏感文件

+ `resources/tencentCloudConfig.properties`

```properties
## 腾讯云配置文件 ##

# 腾讯云账户的SecretId和SecretKey（权限最高）
secret.id=
secret.key=

# 腾讯云短信服务APP信息
app.name=
app.id=
app.key=
app.createTime=

# 短信签名、模板ID、模板参数数量
sms.sign=
sms.template.id.objectMonitor=
sms.template.param.length.objectMonitor=
```

+ `resources/aliCloudConfig.properties`

```properties
## 阿里云配置文件 ##

# 阿里云天气接口appKey
ali.weatherAPI.app.key=
ali.weatherAPI.app.secret=
ali.weatherAPI.app.code=

# 阿里云OSS子用户（OSSUserTwo）
ali.subUser.accessKey.id=
ali.subUser.accessKey.secret=

# 阿里云OSS配置
ali.oss.endPoint=
ali.oss.bucketName=
ali.oss.folderKey=
```

+ `resources/auth.properties`

```properties
# 自建API认证密钥（功能等同于阿里云的 APP Key 和 APP Secret）
key=
secret=
```

