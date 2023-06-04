package com.itbaizhan.shopping_manager_api.controller;

import com.itbaizhan.shopping_common.result.BaseResult;
import com.itbaizhan.shopping_common.service.FileService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/file")
public class FileController {

    @DubboReference
    private FileService fileService;

    @PostMapping("/uploadImage")
    public BaseResult<String> upload(MultipartFile file) throws IOException {
        // 将上传图片转为字节数组，方便在服务间传递
        byte[] bytes = file.getBytes();
        String url = fileService.uploadImage(bytes, file.getOriginalFilename());
        return BaseResult.ok(url);
    }

    @DeleteMapping("/delete")
    public BaseResult delete(String filePath){
        fileService.delete(filePath);
        return BaseResult.ok();
    }
}
