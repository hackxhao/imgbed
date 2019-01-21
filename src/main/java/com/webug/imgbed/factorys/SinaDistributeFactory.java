package com.webug.imgbed.factorys;

import com.github.echisan.wbp4j.Entity.ImageInfo;
import com.github.echisan.wbp4j.UploadRequest;
import com.github.echisan.wbp4j.UploadRequestBuilder;
import com.github.echisan.wbp4j.UploadResponse;
import com.github.echisan.wbp4j.exception.Wbp4jException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description 新浪图片分发
 * @time 2019年1月18日10:42:30
 * @author ahao
 */
@Component
public class SinaDistributeFactory extends DistributeFactory {

    @Value("${sina.username}")
    private String userName;

    @Value("${sina.password}")
    private String passWord;

    private static final Logger logger = LoggerFactory.getLogger(SinaDistributeFactory.class);

    @Override
    public List<String> imgDistribute(MultipartFile[] files) throws IOException {

        UploadRequest uploadRequest = new UploadRequestBuilder()
                .setAcount(userName, passWord)
                .build();
        File f = null;
        FileOutputStream fos = null;
        List<String> urlList = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];
            f = new File (file.getName());
            fos = new FileOutputStream(f);
            fos.write(file.getBytes());
            fos.close();
            try{
                UploadResponse response = uploadRequest.upload(f);
                ImageInfo info = response.getImageInfo();
                urlList.add("https:" + info.getMiddle());
            } catch (Wbp4jException e) {
                e.printStackTrace();
            }
        }
        logger.error("新浪图片分发完毕，共有{}张图片",urlList.size());
        return urlList;
    }
}
