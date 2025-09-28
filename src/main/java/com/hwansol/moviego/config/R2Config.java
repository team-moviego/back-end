package com.hwansol.moviego.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class R2Config {

    @Value("${r2.endpointUrl}")
    private String r2EndpointUrl;

    @Value("${r2.region}")
    private String r2Region;

    @Value("${r2.accessKey}")
    private String r2AccessKey;

    @Value("${r2.secretKey}")
    private String r2SecretKey;

    @Bean
    public AmazonS3 r2Client() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(r2AccessKey, r2SecretKey);

        return AmazonS3ClientBuilder
                .standard()
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(r2EndpointUrl, r2Region))
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }
}
