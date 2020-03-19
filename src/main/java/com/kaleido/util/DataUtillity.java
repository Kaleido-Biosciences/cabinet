package com.kaleido.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.commons.compress.utils.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class DataUtillity {
    
    public static int getPlatesCount(String encodedZipString){
        int count = 0;
        try{
            //taking raw json as zip code is not ready 
            String decodedZipString = encodedZipString;
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(decodedZipString);
            ArrayNode arrayData = (ArrayNode) jsonNode; 
            count = arrayData.size();
        }catch(Exception e) {
            //Need to add logger
            e.printStackTrace();
        }
        return count;
    }
    
    public static String encodeAndZipString(String srcTxt)
        throws IOException {
        ByteArrayOutputStream rstBao = new ByteArrayOutputStream();
        GZIPOutputStream zos = new GZIPOutputStream(rstBao);
        zos.write(srcTxt.getBytes());
        IOUtils.closeQuietly(zos);
        byte[] bytes = rstBao.toByteArray();
        return Base64.getEncoder().encodeToString(bytes);
    }
    /**
     * When client receives the zipped base64 string, it first decode base64
     * String to byte array, then use ZipInputStream to revert the byte array to a
     * string.
     */
    public static String decodeAndUnZipString(String zippedBase64Str)
        throws IOException {
        String result = null;
        byte[] bytes = Base64.getDecoder().decode(zippedBase64Str);
        GZIPInputStream zi = null;
        try {
            zi = new GZIPInputStream(new ByteArrayInputStream(bytes));
            result = new String(IOUtils.toByteArray(zi));
        } finally {
            IOUtils.closeQuietly(zi);
        }
        return result;
    }
}
