package com.kaleido.service.amazonaws.s3;

import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.kaleido.config.Constants;

import liquibase.util.StringUtils;

@Configuration
public class S3Configuration {
    private final Logger log = LoggerFactory.getLogger(S3Configuration.class);
    @Named(Constants.PLATEMAP_BUCKET_SYSTEM_KEY)
    public String bucketName;

    /**
     * Makes a CabinetS3Client available to the Spring Boot framework
     * @return
     */
    @Bean
    public CabinetS3Client getCabinetS3Client(){
        String bucketName = System.getenv(Constants.PLATEMAP_BUCKET_SYSTEM_KEY);
        log.info("Name is: "+ bucketName);
        if(StringUtils.isEmpty(bucketName)){
            log.error("Bucketname not found in system variables with key '"+Constants.PLATEMAP_BUCKET_SYSTEM_KEY+"'. So defaulting to mitlabs");
            bucketName = "mitlabs";
        }
        return new CabinetS3Client( AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).build(), bucketName);
    }
}
