package com.icw.esign.service.cloud.aws;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.icw.esign.service.cloud.CloudFileStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.io.*;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AwsS3Service implements CloudFileStorage {

    @Value("${s3.bucket.name}")
    private String s3BucketName;

    @Value("${s3.bucket.region}")
    private String s3BucketRegion;

    private static final Logger logger = LoggerFactory.getLogger(AwsS3Service.class);

    private AmazonS3 awsS3Client = null;

    @PostConstruct
    private void init() {
        this.awsS3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.fromName(this.s3BucketRegion))
                .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
                .build();
    }

    @Override
    public String uploadFile(String filePath, byte[] fileContent, String contentType) {
        return this.uploadDocument(s3BucketName, filePath, fileContent, contentType);
    }

    @Override
    public byte[] downloadFile(String filePath) {
        return this.getDocument(s3BucketName, filePath);
    }

    @Override
    public void deleteFile(String filePath) {

    }

    private byte[] getDocument(String s3bucket, String key) {
        try {
            S3Object s3Object = this.awsS3Client.getObject(s3bucket, key);
            Map<String, String> attributes = s3Object.getObjectMetadata().getUserMetadata();
            InputStream documentStream = null;
            documentStream = s3Object.getObjectContent();

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            BufferedInputStream bis = new BufferedInputStream(documentStream);
            byte[] buffer = new byte[1024];
            int len;
            while((len=bis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (AmazonS3Exception ex) {
            logger.warn("s3Bucket={}. Key={}", s3bucket, key);
            if (ex.getErrorCode().equalsIgnoreCase("NoSuchBucket")) {
                String msg = String.format("No bucket found with name %s", s3bucket);
                logger.error(msg, ex);
            } else if (ex.getErrorCode().equalsIgnoreCase("AccessDenied")) {
                String msg = String.format("Access denied to S3 bucket %s", s3bucket);
                logger.error(msg, ex);
            }
            logger.error(String.format("Error getting file '%s' from AWS S3 bucket %s", key, s3bucket), ex);
        } catch (Exception ex) {
            logger.warn("s3Bucket={}. Key={}", s3bucket, key);
            logger.error(String.format("Error getting file '%s' from AWS S3 bucket %s", key, s3bucket), ex);
        }
        return null;
    }

    public String getLimitedTimePreSignedUrl(String documentPath, String s3BucketName) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, 5); // Generated URL will be valid for 24 hours
        return this.awsS3Client.generatePresignedUrl(s3BucketName, documentPath, calendar.getTime(), HttpMethod.GET).toString();
    }

    private String uploadDocument(String s3bucket, String key, byte[] fileContent, String contentType) {
        try {
            key = "e-sign/" + key;
            InputStream documentStream = new ByteArrayInputStream(fileContent);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType);
            Map<String, String> attributes = new HashMap<>();
            attributes.put("document-content-size", String.valueOf(fileContent.length));
            metadata.setUserMetadata(attributes);
            PutObjectResult result = this.awsS3Client.putObject(new PutObjectRequest(s3bucket, key, documentStream, metadata));
            logger.info("Saved successfully to S3 bucket with keyName={}", key);
            return key;
        } catch (AmazonS3Exception ex) {
            logger.warn("s3Bucket={}. Key={}", s3bucket, key);
            if (ex.getErrorCode().equalsIgnoreCase("NoSuchBucket")) {
                String msg = String.format("No bucket found with name %s", s3bucket);
                logger.error(msg, ex);
            } else if (ex.getErrorCode().equalsIgnoreCase("AccessDenied")) {
                String msg = String.format("Access denied to S3 bucket %s", s3bucket);
                logger.error(msg, ex);
            }
            logger.error(String.format("Error saving file %s to AWS S3 bucket %s", key, s3bucket), ex);
        } catch (Exception ex) {
            logger.warn("s3Bucket={}. Key={}", s3bucket, key);
            logger.error(String.format("Error saving file %s to AWS S3 bucket %s", key, s3bucket), ex);
        }
        return null;
    }
}
