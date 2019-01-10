package com.webug.imgbed.controller;

import com.aliyun.oss.OSSClient;
import com.webug.imgbed.common.RspEntity;
import com.webug.imgbed.util.AliOSSUtil;
import com.webug.imgbed.util.imgBedUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.*;

@Controller
public class uploadController {

    private static final Logger logger = LoggerFactory.getLogger(uploadController.class);

    //文件存储目录
    private static final String FILE_DIR = "imgBed/";
    // bucket名称
    private static final String BUCKET_NAME = "webug";
    // 外网访问http头
    private static final String HTTP_PATH = "http://webug.oss-cn-beijing.aliyuncs.com/";

    private static final String FILE_EXTENSION = ".png";

    @RequestMapping("/upload")
    public String toUploadPage(){
        return "upload";
    }

    @RequestMapping("/batchUpload")
    @ResponseBody
    public RspEntity batchUpload(@RequestParam(value="file") MultipartFile[] files) throws IOException {
        RspEntity rspEntity = new RspEntity();
        OSSClient ossClient = AliOSSUtil.getOSSClient();
        List<Map<String, Object>> pathList = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            Map<String, Object> pathData = new HashMap<String, Object>();
            MultipartFile file = files[i];
            String fileType = file.getContentType();
            if(StringUtils.isEmpty(file.getOriginalFilename())){
                logger.error("请选择文件");
                rspEntity.setRspCode("999");
                rspEntity.setRspMsg("请选择文件");
                return rspEntity;
            }
            if(StringUtils.isEmpty(fileType) || !fileType.matches("image.*")){
                logger.error("上传图片类型错误:" + fileType);
                rspEntity.setRspCode("999");
                rspEntity.setRspMsg("上传图片类型错误:" + fileType);
                return rspEntity;
            }
            DecimalFormat df = new DecimalFormat("#.00");
            String fileSize = df.format(file.getSize() / 1024 / 1024) + "MB";
            if(file.getSize() > (long)(2 * 1024 * 1024)){
                logger.error("上传图片大小超过限制:" + file.getSize());
                rspEntity.setRspCode("999");
                rspEntity.setRspMsg("上传图片大小超过限制" +"\r文件名称：" + file.getOriginalFilename() +"\r大小：" + fileSize);
                return rspEntity;
            }
            String temp[] = file.getOriginalFilename().split("\\.");
            if (temp.length < 2 || !temp[temp.length - 1].matches("(jpg|jpeg|png|JPG|JPEG|PNG)")) {
                logger.error("上传图片文件名错误:" + file.getOriginalFilename());
                rspEntity.setRspCode("999");
                rspEntity.setRspMsg("上传图片文件名错误:" + file.getOriginalFilename());
                return rspEntity;
            }
            InputStream input = file.getInputStream();
            String fileName = imgBedUtil.getNowDate("yyyyMMddhhmmssSSS") + FILE_EXTENSION;
            // 上传至阿里云oss
            AliOSSUtil.uploadByInputStream(ossClient,input,BUCKET_NAME,FILE_DIR + fileName);
            pathData.put("imagePath",HTTP_PATH + FILE_DIR + fileName);
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
        return rspEntity;
    }
}
