package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

/**
 * 该文件上传类 是将文件缓存到tomcat服务器upload目录 再转存到FTP服务器的img路径下
 * 成功会返回文件名   失败返回空
 */
@Service("iFileService")
public class FileServiceImpl implements IFileService {

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    @Override
    public String upload(MultipartFile file, String path) {
        //获取原始文件名
        String fileName = file.getOriginalFilename();
        //扩展名  ab.jpg   subString取值范围[x,y)   lastIndexOf获取指定最后一次出现"."的下标
        String fileExtendsionName = fileName.substring(fileName.lastIndexOf(".")+1);
        String uploadFileName = UUID.randomUUID().toString()+"."+fileExtendsionName;
        logger.info("开始上传文件,上传的文件名:{},上传的路径:{},新文件名:{}",fileName,path,uploadFileName);

        //加载上传路径 这里是tomcat根目录path
        File fileDir = new File(path);
        if(!fileDir.exists()){
            //设置文件可写
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }

        //目标文件对象构造好路径和上传的名称
        File targetFile = new File(path,uploadFileName);


        try {
            //将文件转移至   至此上传到tomcat成功
            file.transferTo(targetFile);

            //上传至FTP服务器上   此时targetFile的path格式d:tomcat/root/....
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));

        } catch (Exception e) {
            logger.error("上传文件异常",e);
            return null;
        }
        //abc.jpg
        return targetFile.getName();
    }
}
