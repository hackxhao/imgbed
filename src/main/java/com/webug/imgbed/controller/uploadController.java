package com.webug.imgbed.controller;

import com.aliyun.oss.OSSClient;
import com.webug.imgbed.common.AliOSSConstants;
import com.webug.imgbed.util.AliOSSUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class uploadController {

    @Value("${upload.path}")
    private String uploadPath;

    private static final Logger logger = LoggerFactory.getLogger(uploadController.class);

    private OSSClient ossClient = AliOSSUtil.getOSSClient();

    //文件存储目录
    private String filedir = "imgBed/";
    // bucket名称
    private String bucketName = "webug";
    // 外网访问http头
    private String httpPath = "http://webug.oss-cn-beijing.aliyuncs.com/";

    @RequestMapping("/upload")
    public String toUploadPage(){
        return "upload";
    }

    @RequestMapping("/batchUpload")
    public String batchUpload(@RequestParam(value="file") MultipartFile[] files,Model model) throws IOException {
        List<Map<String, Object>> pathList = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            Map<String, Object> pathData = new HashMap<String, Object>();
            MultipartFile file = files[i];
            String fileType = file.getContentType();
            if(StringUtils.isEmpty(fileType) || !fileType.matches("image.*")){
                logger.error("上传图片类型错误:" + fileType);
                continue;
            }
            if(file.getSize() > (long)(2 * 1024 * 1024)){
                logger.error("上传图片大小超过限制:" + file.getSize());
                continue;
            }
            String fileName = file.getOriginalFilename();
            String temp[] = fileName.split("\\.");
            if (temp.length < 2 || !temp[temp.length - 1].matches("(jpg|jpeg|png|JPG|JPEG|PNG)")) {
                logger.error("上传图片文件名错误:" + fileName);
                continue;
            }
            InputStream input = file.getInputStream();
            // 上传至阿里云oss
            AliOSSUtil.uploadByInputStream(ossClient,input,bucketName,filedir + fileName);
            pathData.put("imagePath",httpPath + filedir + fileName);
            pathList.add(pathData);
        }
        model.addAttribute("pathList",pathList);
        ossClient.shutdown();
        return "success";
    }
}
