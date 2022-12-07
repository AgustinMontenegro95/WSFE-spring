package com.prueba.pruebaAPI.funciones.S3;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectTaggingRequest;
import com.amazonaws.services.s3.model.DeleteObjectTaggingResult;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.ObjectTagging;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.Tag;

import org.springframework.stereotype.Service;

@Service
public class S3ManagerUtils {
    private final AmazonS3 s3;
    private final static String BUCKET_NAME = "factura-e";
    

    public S3ManagerUtils() {
        s3 = new AmazonConfig().s3();
    }

    
    public boolean uploadFile(String keyName, String fileName) {
        boolean success = false;
        try {
            s3.putObject(BUCKET_NAME, keyName, new File(fileName));
            success = true;
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
        } 

        return success;
    }
    
    public boolean uploadMultipartFile(String fileName, InputStream inputStream, String contentType, boolean shouldExpire) {
        boolean success = false;
        if (inputStream != null) {
            try {
                ObjectMetadata meta = new ObjectMetadata();
                meta.setContentLength(inputStream.available());
                meta.setContentType(contentType);

                PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME, fileName, inputStream, meta);
                putObjectRequest = putObjectRequest.withCannedAcl(CannedAccessControlList.Private);

                if (shouldExpire) {
                    putObjectRequest.setTagging(generateAutomaticExpirationTag());
                }

                PutObjectResult result = s3.putObject(putObjectRequest);
                if (result != null) {
                    success = true;
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("InputStream es null. No se subira contenido a S3");
        }

        return success;
    }

    public void removeTagsFromClientImages(String documentNumber) {
        String keyPrefix = "resources/images/";
        deleteObjectTags(keyPrefix + documentNumber + "/front.jpg");
        deleteObjectTags(keyPrefix + documentNumber + "/back.jpg");
        deleteObjectTags(keyPrefix + documentNumber + "/selfie.jpg");
    }

    public String generatePresignedURL(String keyName) {
        String presignedUrl = "";
        
        try {
            // Set the presigned URL to expire after one minute.
            Date expiration = new Date();
            long expTimeMillis = Instant.now().toEpochMilli();
            expTimeMillis += 259200 * 60;
            expiration.setTime(expTimeMillis);

            // Generate the presigned URL.
            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(BUCKET_NAME, keyName)
                            .withMethod(HttpMethod.GET)
                            .withExpiration(expiration);
            URL url = s3.generatePresignedUrl(generatePresignedUrlRequest);
            presignedUrl = url.toString();
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process 
            // it, so it returned an error response.
            System.out.println(e.getMessage());
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            System.out.println(e.getMessage());
        }
        
        return presignedUrl;
    }

    private void deleteObjectTags(String keyName) {
        DeleteObjectTaggingRequest request = new DeleteObjectTaggingRequest(BUCKET_NAME, keyName);
        s3.deleteObjectTagging(request);
    }

    private ObjectTagging generateAutomaticExpirationTag() {
        List<Tag> tags = new ArrayList<Tag>();
        tags.add(new Tag("automatic-deletion", "1"));
        ObjectTagging tagging = new ObjectTagging(tags);

        return tagging;
    }
}
