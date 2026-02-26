package com.pieropan.notifacao.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSAsyncClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonSnsConfiguration {

    @Value("${aws.acessKey}")
    private String accessKey;

    @Value("${aws.secretKey}")
    private String secretKey;

    @Bean
    public AWSCredentials awsCredentials () {
        return new BasicAWSCredentials(accessKey, secretKey);
    }

    public AmazonSNS amazonSNS() {
        return AmazonSNSAsyncClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials()))
                .withRegion(Regions.US_EAST_1).build();
    }
    
}
