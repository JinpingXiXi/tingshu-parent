package com.atguigu.tingshu.album.service.impl;

import com.atguigu.tingshu.album.service.FileService;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {
        
        //
        @Value("${minio.endpointUrl}")
        private String endpointUrl;
        @Value("${minio.accessKey}")
        private String accessKey;
        @Value("${minio.secreKey}")
        private String secreKey;
        @Value("${minio.bucketName}")
        private String bucketName;

        @Override
        public String fileUpload(MultipartFile fileForUpload) {
                String url = "";
                
                //build minIO client for IO
                MinioClient minioClient =
                        MinioClient.builder()
                                .endpoint(endpointUrl)
                                .credentials(accessKey,secreKey)
                                .build();
                
                try {//bucket creation & IO operations
                        
                        //make the bucket if not already exist
                        
                        boolean isFound =
                                minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
                        if (!isFound) {
                                minioClient.makeBucket(
                                        MakeBucketArgs.builder().bucket(bucketName).build()
                                );
                        }else {
                                System.out.println("Bucket: "+bucketName+" is already exists");
                        }
                        
                        /**
                         * Object-A: upload the file into bucket,
                         * Object-B: concat the url for return;
                         */
                        //A1.generate a fileName (non-duplicate)
                        String fileName = UUID.randomUUID()
                                .toString()
                                .replaceAll("-","")
                                + "."
                                + FilenameUtils.getExtension(
                                        fileForUpload.getOriginalFilename()
                                );
                        
                        //A2.put it to bucket
                        minioClient.putObject(
                                PutObjectArgs.builder()
                                        .bucket(bucketName)
                                        .object(fileName)
                                        .stream(fileForUpload.getInputStream(),fileForUpload.getSize(),-1)
                                        .build()
                        );
                        
                        //B.concat the url for return
                        url = endpointUrl+"/"+bucketName+"/"+fileName;
                        System.out.println("url = " + url);
                        
                        return url;
        
                } catch (ErrorResponseException e) {
                        throw new RuntimeException(e);
                } catch (InsufficientDataException e) {
                        throw new RuntimeException(e);
                } catch (InternalException e) {
                        throw new RuntimeException(e);
                } catch (InvalidKeyException e) {
                        throw new RuntimeException(e);
                } catch (InvalidResponseException e) {
                        throw new RuntimeException(e);
                } catch (IOException e) {
                        throw new RuntimeException(e);
                } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                } catch (ServerException e) {
                        throw new RuntimeException(e);
                } catch (XmlParserException e) {
                        throw new RuntimeException(e);
                }
        
        
                
        }





}
