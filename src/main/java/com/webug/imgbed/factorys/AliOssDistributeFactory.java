package com.webug.imgbed.factorys;

import com.aliyun.oss.OSSClient;
import com.webug.imgbed.util.AliOSSUtil;
import com.webug.imgbed.util.IMGBedUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description 阿里云oss图片分发
 * @time 2019年1月21日14:04:55
 * @author ahao
 */

@Component
public class AliOssDistributeFactory extends DistributeFactory {

    private static final Logger logger = LoggerFactory.getLogger(DistributeFactory.class);

    //文件存储目录
    private static final String FILE_DIR = "imgBed/";
    // bucket名称
    private static final String BUCKET_NAME = "webug";
    // 外网访问http头
    private static final String HTTP_PATH = "https://webug.oss-cn-beijing.aliyuncs.com/";

    private static final String FILE_EXTENSION = ".png";

    OSSClient ossClient = AliOSSUtil.getOSSClient();

    @Override
    public List<String> imgDistribute(MultipartFile[] files) throws IOException {
        List<String> urlList = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];
            InputStream input = file.getInputStream();
            String fileName = IMGBedUtil.getNowDate("yyyyMMddhhmmssSSS") + FILE_EXTENSION;
            AliOSSUtil.uploadByInputStream(ossClient,input,BUCKET_NAME,FILE_DIR + fileName);
            urlList.add(HTTP_PATH + FILE_DIR + fileName);
        }
        logger.error("阿里云OSS图片分发完毕，共有{}张图片",urlList.size());
        return urlList;
    }
}
