package com.atguigu.tingshu.album.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


public interface FileService {

String fileUpload(MultipartFile file);

}
