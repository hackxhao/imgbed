package com.webug.imgbed.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class AliOSSConstants {

    private static String endpoint;

    private static String accessKeyId;

    private static String accessKeySecret;

    /**
     * 从yml读取
     * @return
     */
    @Value("${aliyun.config.ossEndPoint}")
    private String endpointYml;

    @Value("${aliyun.config.accessKeyId}")
    private String accessKeyIdYml;

    @Value("${aliyun.config.accessKeySecret}")
    private String accessKeySecretYml;


    //利用@PostConstruct将yml中配置的值赋给本地的变量
    @PostConstruct
    public void initParameter(){
        endpoint = this.endpointYml;
        accessKeyId = this.accessKeyIdYml;
        accessKeySecret = this.accessKeySecretYml;
    }

    public static String getEndpoint() {
        return endpoint;
    }

    public static String getAccessKeyId() {
        return accessKeyId;
    }

    public static String getAccessKeySecret() {
        return accessKeySecret;
    }
}
