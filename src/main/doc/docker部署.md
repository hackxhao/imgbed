#### 第一步：pom.xml加入docker-maven依赖
```

<properties>
    <docker.image.prefix>镜像名称</docker.image.prefix>
</properties>

<!--加入maven插件“docker-maven-plugin”-->
<plugin>
    <groupId>com.spotify</groupId>
    <artifactId>docker-maven-plugin</artifactId>
    <version>0.4.11</version>
    <configuration>
        <imageName>${docker.image.prefix}/${project.artifactId}</imageName>
        <dockerDirectory>src/main/docker</dockerDirectory>
        <resources>
            <resource>
                <targetPath>/</targetPath>
                <directory>${project.build.directory}</directory>
                <include>${project.build.finalName}.jar</include>
            </resource>
        </resources>
    </configuration>
</plugin>
```

#### 第二步：创建Dockerfile文件


在`src/main/`下面新建docker文件夹并在里面创建Dockerfile文件

文件内容具体如下：
```
#基础镜像使用jdk8，如果容器中没有，docker会自动下载安装
FROM java:8

# VOLUME 指定了临时文件目录为/tmp。
# 其效果是在主机 /var/lib/docker 目录下创建了一个临时文件，并链接到容器的/tmp
VOLUME /tmp 

# 将jar包添加到容器中并更名为app.jar
ADD imgbed-0.0.1-SNAPSHOT.jar app.jar

# 运行jar包
RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
```

第三步：项目打成jar包


我这里以idea为例


![](https://webug.oss-cn-beijing.aliyuncs.com/imgBed/20190124091700123.png)

![](https://webug.oss-cn-beijing.aliyuncs.com/imgBed/20190124091700184.png)

第四步：将Dockerfile和jar上传至服务器

![](https://webug.oss-cn-beijing.aliyuncs.com/imgBed/20190124092429413.png)



第五步：进入Docker目录进行build

![](https://webug.oss-cn-beijing.aliyuncs.com/imgBed/20190124092429455.png)

圈红部分需要注意下...

通过`docker build -t img-bed .`命令进行镜像构建

查看镜像`docker images`

![](https://webug.oss-cn-beijing.aliyuncs.com/imgBed/20190124092429476.png)


第六步：运行


![](https://webug.oss-cn-beijing.aliyuncs.com/imgBed/20190124092429488.png)

通过`docker run -d -p 8080:8080 img-bed`进行启动


![](https://webug.oss-cn-beijing.aliyuncs.com/imgBed/20190124092429501.png)

