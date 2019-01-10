### SpringBoot+阿里云OSS图床项目

从 Github 上下载

```shell
git clone https://github.com/hackxhao/imgbed.git
```

#### 快速运行
##### 准备工作
```shell
JDK >= 1.8 (推荐1.8版本)
Maven >= 3.0
```
##### 修改配置
application.yml文件
```shell
aliyun:
  config:
    ossEndPoint: 您自己的地域节点
    accessKeyId: 您阿里云的accessKeyId
    accessKeySecret: 您阿里云的accessKeyId
```
uploadController.java
```shell
//文件存储目录
private String filedir = "***/";
// bucket名称
private String bucketName = "***";
// 外网访问http头
private String httpPath = "***";
```
执行ImgbedApplication的Main方法即可启动


#### Todo
- 添加水印
- 优化页面
- 增加文件重复校验
- 增加Gitalk插件

#### 演示图
![](http://webug.oss-cn-beijing.aliyuncs.com/imgBed/QQ截图20190109185254.png)



