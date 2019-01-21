package com.webug.imgbed.factorys;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @Description 图片分发工厂
 * @time 2019年1月18日10:25:32
 * @author ahao
 */
public abstract class DistributeFactory {

    /**
     * 分发图片
     */
    public abstract List<String> imgDistribute(MultipartFile[] files) throws IOException;
}
