package com.kaleido.service.amazonaws.s3;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectResult;

public class CabinetS3Client {

    private final AmazonS3 s3;
    private final Logger log = LoggerFactory.getLogger(CabinetS3Client.class);
    private final String bucketName;

    public CabinetS3Client(AmazonS3 s3, String bucketName) {
        this.s3 = s3;
        this.bucketName = bucketName;
    }

    /**
     * Puts the object in jsonString format to Amazon s3
     * @param key
     * @param content
     * @return
     * @throws CabinetS3Exception
     */
    public void writeToS3(@NotNull String key, @NotNull String content) throws CabinetS3Exception {
        try{
            s3.putObject(bucketName, key, content);
        }catch(Exception e) {
            log.error("Error occured in putting the object to Amazon S3:"+e.getMessage());
            throw new CabinetS3Exception(
                    "Error occured in putting the object to Amazon S3:"+e.getMessage());
        }
    }
}