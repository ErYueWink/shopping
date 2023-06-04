package com.itbaizhan.shopping_common.service;

// 文件服务
public interface FileService {
    // 上传图片
    String uploadImage(byte[] fileImage,String fileName);
    // 删除图片
    void delete(String filePath);
}
