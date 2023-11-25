package com.atguigu.tingshu.album.api;

import com.atguigu.tingshu.album.config.MinioConstantProperties;
import com.atguigu.tingshu.album.service.FileService;
import com.atguigu.tingshu.common.result.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "上传管理接口")
@RestController
@RequestMapping("api/album")
public class FileUploadApiController {

        @Autowired
        private MinioConstantProperties minioConstantProperties;
        @Autowired
        private FileService fileService;

        @PostMapping("fileUpload")
        public Result fileUpload(MultipartFile file){
                String url = fileService.fileUpload(file);
                return Result.ok(url);
        }

        public static void main(String[] args) {
                String fileName = "atg.uigu.jpg";
                //  第一种就是string 常用方法截取. lastIndexOf(".") subString();
                //  第二种方案就是使用工具类获取文件后缀。
                String extension = FilenameUtils.getExtension(fileName);
                System.out.println(extension);
                
        }
        
}
