package com.webug.imgbed.controller;

import com.webug.imgbed.common.RspEntity;
import com.webug.imgbed.factorys.AliOssDistributeFactory;
import com.webug.imgbed.factorys.SinaDistributeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class uploadController {

    private static final Logger logger = LoggerFactory.getLogger(uploadController.class);

    @Autowired
    private SinaDistributeFactory sinaDistributeFactory;

    @Autowired
    private AliOssDistributeFactory aliOssDistributeFactory;

    @RequestMapping("/upload")
    public String toUploadPage(){
        return "upload";
    }

    @RequestMapping("/batchUpload")
    @ResponseBody
    public RspEntity batchUpload(@RequestParam(value="file") MultipartFile[] files, HttpServletRequest request) throws IOException {
        RspEntity rspEntity = new RspEntity();
        HttpSession session = request.getSession();
        List<String> fileNameList = new ArrayList<>();
        if(files.length > 10){
            rspEntity.setRspCode("999");
            rspEntity.setRspMsg("单次删除不得超过10张图片");
            return rspEntity;
        }
        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];
            fileNameList.add(file.getOriginalFilename());
        }
        if(!checkSessionFile(session,fileNameList)){
            logger.error("您上传的文件已存在");
            rspEntity.setRspCode("999");
            rspEntity.setRspMsg("您上传的文件至少有一张已存在，请去除后重新上传");
            return rspEntity;
        }
        for(int i = 0; i < files.length; i++){
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
                logger.error("上传图片大小超过限制:" + fileSize);
                rspEntity.setRspCode("999");
                rspEntity.setRspMsg("上传图片大小超过限制:" + fileSize);
                return rspEntity;
            }
            String temp[] = file.getOriginalFilename().split("\\.");
            if (temp.length < 2 || !temp[temp.length - 1].matches("(jpg|jpeg|png|JPG|JPEG|PNG)")) {
                logger.error("上传图片文件名错误:" + file.getOriginalFilename());
                rspEntity.setRspCode("999");
                rspEntity.setRspMsg("上传图片文件名错误:" + file.getOriginalFilename());
                return rspEntity;
            }
        }
        logger.error("所有校验通过，开始进行图片分发...");
        // 新浪分发
        List<String> sinaPathList = sinaDistributeFactory.imgDistribute(files);

        // 阿里OSS分发
        List<String> aliOssPathList = aliOssDistributeFactory.imgDistribute(files);

        session.setAttribute(session.getId(),fileNameList);
        if(sinaPathList.size() > 0 || aliOssPathList.size() > 0){
            Map<String, Object> data = new HashMap<>();
            data.put("sinaPathList",sinaPathList);
            data.put("aliOssPathList",aliOssPathList);
            Map<String, Object> rspData = new HashMap<String, Object>();
            rspData.put("data",data);
            rspEntity.setRspCode("000");
            rspEntity.setRspMsg("上传成功！！！");
            rspEntity.setRspData(rspData);
        }else{
            rspEntity.setRspCode("999");
            rspEntity.setRspMsg("上传失败！！！");
        }
        return rspEntity;
    }

    /**
     * 通过sessionId进行校验文件是否存在
     * @param session
     * @param fileList
     * @return
     */
    public Boolean checkSessionFile(HttpSession session, List<String> fileList) {
        List<String> fileNameList = (List) session.getAttribute(session.getId());
        if (null != fileNameList && fileNameList.size() > 0) {
            for (String fileName : fileNameList) {
                for (String file : fileList) {
                    if (fileName.equals(file)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
