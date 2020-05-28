package com.kaleido.service.amazonaws.s3;

/**
 * An Exception indicating a problem with Kapture's use of S3
 */
public class CabinetS3Exception extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    CabinetS3Exception(String message, Throwable cause){
        super(message, cause);
    }

    CabinetS3Exception(String message) {
        super(message);
    }
}
