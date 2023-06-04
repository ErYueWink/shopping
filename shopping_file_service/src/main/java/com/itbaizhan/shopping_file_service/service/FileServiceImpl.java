package com.itbaizhan.shopping_file_service.service;

import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.itbaizhan.shopping_common.exception.BusException;
import com.itbaizhan.shopping_common.result.CodeEnum;
import com.itbaizhan.shopping_common.service.FileService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@DubboService
public class FileServiceImpl implements FileService {

    @Autowired
    private FastFileStorageClient fastFileStorageClient;
    @Value("${fdfs.fileUrl}")
    private String fileUrl;

    @Override
    public String uploadImage(byte[] fileImage, String fileName) {
        if(fileImage != null){
            try{
                // 将图片字节数组转为输入流
                InputStream inputStream = new ByteArrayInputStream(fileImage);
                // 获取文件后缀名
                String substring = fileName.substring(fileName.lastIndexOf("." )+1);
                // 上传文件
                StorePath storePath = fastFileStorageClient.uploadFile(inputStream, inputStream.available(), substring, null);
                // 返回文件路径
                String path = fileUrl + "/" +storePath.getFullPath();
                return path;
            }catch (IOException e){
                throw new BusException(CodeEnum.UPLOAD_FILE_ERROR);
            }
        }else{
            throw new BusException(CodeEnum.UPLOAD_FILE_ERROR);
        }


    }

    @Override
    public void delete(String filePath) {
        fastFileStorageClient.deleteFile(filePath);
    }
}
