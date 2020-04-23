package com.kaleido.service.amazonaws.s3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.services.s3.AmazonS3;

@Configuration
public class S3Configuration {
    private final Logger log = LoggerFactory.getLogger(S3Configuration.class);

    @Value("${platemap.export.bucket}")
    private String bucketName;

    @Autowired
    AmazonS3 s3;

    /**
     * Makes a CabinetS3Client available to the Spring Boot framework
     * @return
     */
    @Bean
    public CabinetS3Client getCabinetS3Client(){
        log.debug("Platemap export buckent name :"+bucketName);
        return new CabinetS3Client( s3, bucketName);
    }
}
