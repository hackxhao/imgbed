package com.webug.imgbed.controller;

import com.aliyun.oss.OSSClient;
import com.webug.imgbed.common.RspEntity;
import com.webug.imgbed.util.AliOSSUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

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

    //文件存储目录
    private String filedir = "***/";
    // bucket名称
    private String bucketName = "***";
    // 外网访问http头
    private String httpPath = "***";

    @RequestMapping("/upload")
    public String toUploadPage(){
        return "upload";
    }

    @RequestMapping("/batchUpload")
    @ResponseBody
    public RspEntity batchUpload(@RequestParam(value="file") MultipartFile[] files, Model model) throws IOException {
        RspEntity rspEntity = new RspEntity();
        OSSClient ossClient = AliOSSUtil.getOSSClient();
        List<Map<String, Object>> pathList = new ArrayList<>();
        if(files.length > 0){
            for (int i = 0; i < files.length; i++) {
                Map<String, Object> pathData = new HashMap<String, Object>();
                MultipartFile file = files[i];
                String fileType = file.getContentType();
                if(StringUtils.isEmpty(fileType) || !fileType.matches("image.*")){
                    logger.error("上传图片类型错误:" + fileType);
                    rspEntity.setRspCode("999");
                    rspEntity.setRspMsg("上传图片类型错误:" + fileType);
                    return rspEntity;
                }
                if(file.getSize() > (long)(2 * 1024 * 1024)){
                    logger.error("上传图片大小超过限制:" + file.getSize());
                    rspEntity.setRspCode("999");
                    rspEntity.setRspMsg("上传图片大小超过限制-" +"文件：" + file.getOriginalFilename() +"大小："+ file.getSize());
                    return rspEntity;
                }
                String fileName = file.getOriginalFilename();
                String temp[] = fileName.split("\\.");
                if (temp.length < 2 || !temp[temp.length - 1].matches("(jpg|jpeg|png|JPG|JPEG|PNG)")) {
                    logger.error("上传图片文件名错误:" + fileName);
                    rspEntity.setRspCode("999");
                    rspEntity.setRspMsg("上传图片文件名错误:" + fileName);
                    return rspEntity;
                }
                InputStream input = file.getInputStream();
                // 上传至阿里云oss
                AliOSSUtil.uploadByInputStream(ossClient,input,bucketName,filedir + fileName);
                pathData.put("imagePath",httpPath + filedir + fileName);
                pathList.add(pathData);
            }
            if(pathList.size() > 0){
                Map<String, Object> rspData = new HashMap<String, Object>();
                rspData.put("dataList",pathList);
                rspEntity.setRspCode("000");
                rspEntity.setRspMsg("上传成功！！！");
                rspEntity.setRspData(rspData);
            }else{
                rspEntity.setRspCode("999");
                rspEntity.setRspMsg("上传失败！！！");
            }
        }else{
            rspEntity.setRspCode("999");
            rspEntity.setRspMsg("请选择文件");
        }
        return rspEntity;
    }
}
